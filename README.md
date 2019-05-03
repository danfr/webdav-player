# webdav-player
Simple access to remote WebDAV repository and play media files using VLC streaming client. This project is a MVP with basic UI and features.

## Usage
### Prerequisites
To use this application, you need to have the following components installed locally :
* Java Runtime Environment 1.8+
* VLC Media Player 2.2+

### Getting started
* Download the latest release (executable JAR) from this repository
* Create a file named **webdav-player.properties** based upon the template [here](https://github.com/danfr/webdav-player/blob/master/src/main/resources/webdav-player.properties) in the same directory as the JAR. In this file, set the full path to VLC binary **(mandatory)** and default Server URL and username (optional).
* Run the JAR (double click on Windows, or `java -jar webdav-player-exectuable.jar`)

### How to play files
* Enter your WebDAV Server URL and credentials and then click "Connect"
* You now should see the list of your server's files and browse it by double clicking on directories
* To play media files, simply double click on the filename. It should be opened by VLC on the streaming way (so the file is not downloaded locally). All media files supported by VLC can be played.
