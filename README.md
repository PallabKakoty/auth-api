
# Authentication API #


# MySql DB Tables

	
	* Database Name: auth-api
	
	CREATE TABLE IF NOT EXISTS `users` (
  	`id` int(11) NOT NULL AUTO_INCREMENT,
  	`name` varchar(250) NOT NULL,
  	`email` varchar(250) NOT NULL,
  	`password` varchar(250) NOT NULL,
  	`session_token` varchar(250) NOT NULL,
  	`session_token_date` datetime NOT NULL,
  	`created_at` datetime NOT NULL,
  	`login_at` datetime NOT NULL,
  	`status` tinyint(4) NOT NULL,
  	PRIMARY KEY (`id`));
	
	CREATE TABLE IF NOT EXISTS `user_verification` (
  	`id` int(11) NOT NULL AUTO_INCREMENT,
  	`userId` int(11) NOT NULL,
  	`verificationToken` varchar(250) NOT NULL,
  	`status` tinyint(4) NOT NULL,
  	`created_at` datetime NOT NULL,
  	PRIMARY KEY (`id`));


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
            
      
      
      

