package com.daimler.challenge.urlprocessor;

import java.nio.charset.StandardCharsets;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

@RestController
@RequestMapping("/process/url")
public class UrlProcessorService {

	    @Autowired
	    StringRedisTemplate redisTemplate;
	    
	    int serviceCount = 0;

	    @GetMapping("/{id}")
	    public String getUrl(@PathVariable String id) {

	        String url = redisTemplate.opsForValue().get(id);
	        System.out.println("URL Retrieved: " + url);

	        if (url == null) {
	            throw new RuntimeException("There is no shorter URL for : " + id);
	        }
	        return url;
	    }

	
	    @PostMapping
	    public String create( @RequestParam(value="url") String url , @RequestParam(value="customUrl", required = false) String customUrl) {

	        UrlValidator urlValidator = new UrlValidator(
	                new String[]{"http", "https"});
	        
	    
	        if (urlValidator.isValid(url)) {
	        	if(customUrl == null) {
	        		String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
		            System.out.println("URL Id generated: "+ id);
		            redisTemplate.opsForValue().set(id, url);
		            serviceCount++;
		            return "Shortened url is : "+id +" and Service count : " +serviceCount;
	        		 
	        	}else {
	        		System.out.println("CustomUrl is: "+ customUrl);
	        		redisTemplate.opsForValue().set(customUrl, url);
	        		serviceCount++;
	        		return "Shortened custom url is "+customUrl +" and Service Count" +serviceCount;
	        	}
	        }
	        
	       
	        
	       
	        throw new RuntimeException("URL Invalid: " + url);
	    }
	    
	   
}
