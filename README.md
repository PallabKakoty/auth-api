
# play-scala-slick Authentication API #


# Create Profile API
  
  	POST			http://localhost:9000/v1/createprofile
	
				{"name" : "Abcd", "email": "abcd@gmail.com","password": "abcd12"}
          			
				* ContentType: application/json (Request Body)
            
        Email will be send to user to verify account. 
        Eg: http://localhost:9000/v1/activateprofile?verifyToken=tfpzgunvaamnblx


# Login API
  
  	POST			http://localhost:9000/v1/login
				
				{"email": "abcd@gmail.com", "password": "abcd12"}
            			
				* ContentType: application/json (Request Body)
				
	 Login API will send back the user detail with access token (session token) after successful login.


# Forgot Password API

	POST			http://localhost:9000/v1/forgotpassword
	
				{"email": "abcd@gmail.com"}
				
				* ContentType: application/json (Request Body)
            
	A link will be sent to the user email id to change password.
	Eg: http://localhost:9000/v1/setpassword?verifyToken=0f4turhiigd6799


# Reset Password API

	POST			http://localhost:9000/v1/resetpassword
				    
				{"email": abcd@gmail.com, “oldPassword”:”abcd12”, “newPassword”:”abcd123”}
				
				* ContentType: application/json (Request Body)
            
      
      
      

