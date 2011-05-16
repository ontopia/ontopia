<framework:meminfo name='overall'>

  <framework:checkUser></framework:checkUser>

    <template:put name='title' body='true'>[Omnigator] Topic No. 1</template:put>

    <template:put name='heading' body='true'>
        <h1 class='boxed'>Topic No. 1</h1>
    </template:put>
    <template:put name='skin' body='true'>skins/<jsp:getProperty name='ontopiaUser' property='skin'></jsp:getProperty>.css</template:put>

    <template:put name='toplinks' body='true'>

      <a href='manage.jsp'>Manage</a>
      | <a href='index.jsp'>Welcome</a>
      | 
  	
  	  
  	
      <framework:pluginList separator=' | '></framework:pluginList>
	    
    </template:put>

	  
    <template:put name='navigation' body='true'>

	
	  
    
        

        <framework:meminfo name='types'>
	
	  
	
	
          
        
	</framework:meminfo>

        

	
	  
	
	
	  
	
	
    	
    	  
        

	 
        

        <framework:meminfo name='assocs'>
	
	  
	
	
	  
	
	
	  
	
	
	  
	
	
        <p><table class='shboxed' width='100%'><tr><td>
    	
    	  
    	    <h3>Related Subjects</h3>
    	    <ul>
    	      
                <li><b>Contained in</b>
  		  
  		    
  		      
  		    
  		  
  		  <ul>
  		    
                      
			
			  
		          
		        
		      
		      
		        
			
			<li type='circle'>Norway (Container)</li>
		      
		        
			
			<li type='circle'>Oslo (Containee)</li>
		      

  		    
  		  </ul>

		</li>
  	      
    	    </ul>
    	  
    	  
        
	</td></tr></table></p>
	</framework:meminfo>

        

	
	  
	
	
	  
	
      	
    	  
        
	    
    </template:put>
    <template:put name='content' body='true'>
      
        

	
	  
	
	
    	
    	  
        
			      
        

        <framework:meminfo name='occurrences'>
	
	  
	
	
	  
	
	
        <p><table class='shboxed' width='100%'><tr><td>
    	
    	  
    	  
    	    No occurrences available for this topic.
    	  
        
	</td></tr></table></p>
	</framework:meminfo>

	
        

        <framework:meminfo name='instances'>
	
	  
	
	
    	
    	  
	
	</framework:meminfo>

        


	
	  
	

	
	  
	

	
	  
	


      	
    	  
        
	    
        

        <framework:meminfo name='roleplayers'>
	
	  
	
	
	  
	
	
    	
    	  
	
	</framework:meminfo>

        

      	
    	  
        

	    
    </template:put>
	  
    <template:put name='outro' body='true'></template:put>
      
    <template:put name='application' content='/fragments/application.jsp'></template:put>
    <template:put name='header-tagline' content='/fragments/tagline-header.jsp'></template:put>
    <template:put name='footer-tagline' content='/fragments/tagline-footer.jsp'></template:put>


</framework:meminfo>