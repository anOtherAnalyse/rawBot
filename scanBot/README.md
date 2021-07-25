### Scan Bot
MC 1.12.2 bot that scans for player presence on given area of the map, using a paper 1.12.2 exploit.

### Build
build the API:  
```rawBot$ mvn clean package```

build the scan bot:  
```rawBot/scanBot$ mvn clean package```

The jar will be generated in ```scanBot/target/scanBot-1.0.jar```

### Run (Linux)
```
rawBot/scanBot$ java -cp ../target/rawBot-1.0.jar:target/scanBot-1.0.jar scanbot.Main <center_x> <center_z> <radius> <host> [port]
```

### Run (Windows)
```
rawBot\scanBot> java -cp "..\target\rawBot-1.0.jar;target\scanBot-1.0.jar" scanbot.Main <center_x> <center_z> <radius> <host> [port]
```

```(center_x, center_z)``` - area center (coords in blocks)  
```radius``` - area radius in blocks  
```host``` - server to connect to  
```port``` - optional server port (default 25565)

At first run the bot will prompt for credentials to use with mojang auth servers. The tokens are then saved in ```token.txt``` for later use.

Maximum area radius should be 100k, the bot will look for players in the area until you stop it. If it finds something it will say so in standard output, which can be redirected to a file.

To confirm these presences rename the scan bot log file to ```targets.txt``` and run:
```
rawBot/scanBot$ java -cp ../target/rawBot-1.0.jar:target/scanBot-1.0.jar scanbot.Main <host> [port]
```

This will loop over the areas of presence defined in ```targets.txt``` and regularly check if someone is here, thus confirming active presence at one place.

### Targets

This bot was built for 1.12.2 paper servers using a queue server. It will wait to pass the queue before starting a scan. You may want to remove this behaviour for use on servers without a queue.
