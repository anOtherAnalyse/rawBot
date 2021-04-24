### Minecraft 1.12.2 Simple bot API
Can be used to create a basic bot for 1.12.2 servers, performing actions by simply receiving and sending the right packets to the server.

#### Build the API
You will need maven: ```$ mvn clean package```  
The jar will be created in the ```target``` folder.

#### Create a bot
Create a class extending ```rawbot.bot.Bot``` and implement the abstract methods.  
Run it by calling ```run``` on your bot from your main method.  

Check ```rawbot/bot/examples/ChatDumpBot.java``` & ```Example.java``` for an example.

#### Run the example
Run the chat dump bot.  

Compile it: ```$ javac Example.java -cp target/rawBot-1.0.jar```  
Run it on a server: ```$ java -cp target/rawBot-1.0.jar:. Example <host> [port]```  

After authentication your tokens will be saved in ```token.txt``` for future uses.
