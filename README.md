# Overview

## Authors

<b>GC34</b> group
- <b>Carlo Sgaravatti</b>
- <b>Jacopo Maria Polverini</b>
- <b>Luca Romano</b>

## Functionalities

The implemented functionalities are:

- Basic Rules
- Complete Rules
- Socket
- CLI 
- GUI
- Multiple games
- Persistence
- Characters (all 12 the character cards)

# Test Coverage

## Coverage Overview
![globalCoverage](https://user-images.githubusercontent.com/58942793/176880809-04461007-64c3-48ed-8356-4d4ccc99608f.png)

## Controller package coverage
![controllerCoverage](https://user-images.githubusercontent.com/58942793/176880826-14c432e8-87c8-46aa-828f-65a739ff5a21.png)

## Model package coverage
![modelCoverage](https://user-images.githubusercontent.com/58942793/176880836-326909e9-ca2a-4af1-97b4-f3e6f8ae42f4.png)

# How to start the game

There is a single jar file named <code>GC34.jar</code> that can be found in the deliverables directory.
To run the jar insert <code>java -jar GC34.jar</code> in the command line.

## Server

The Server can be instanciated by inserting 0 after running the jar. You have to insert the port on which the server will accept connections.

The Server will use a directory named <code>/backupGames</code> on the same directory of the jar to save games on disk. If the directory is not present
it will create it.

## Client

### CLI

The CLI can be runned by inserting 1 after running the jar. For the best game experience, it is suggested to run the CLI in a Linux or Mac full screen shell (in
windows it is possible to use wsl).

The game information will be printed every time a new turn start and, if it is the user turn, everytime the user does an action.

To know how to do actions, simply type Help when it is your turn.

### GUI

The GUI can be runned by inserting 2 after running the jar. For the best game experience, it is suggested to run the GUI in full screen on a 1920x1080 resolution.
The next sections will be useful to know how to do actions during the turn

#### Moving students with drag and drop

https://user-images.githubusercontent.com/58942793/176877162-09eec544-0a8b-4fc2-9728-a814468c6f9a.mp4

#### Moving mother nature with drag and drop

https://user-images.githubusercontent.com/58942793/176877350-5e5a91c7-f9db-4a44-83b2-848ca3229e08.mp4

#### Selecting a cloud

https://user-images.githubusercontent.com/58942793/176877439-190f1d75-36df-49ec-8692-0381bbb87f56.mp4

#### Playing a character card

https://user-images.githubusercontent.com/58942793/176877482-8ebf915b-9257-4209-9e69-b4c67dec6fe0.mp4

Note: this action is not mandatory, therefore it is possible to select the end turn button to end the turn without playing a character card. The end turn will
be automatic if all the actions are done.



