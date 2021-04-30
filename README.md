# Term Project

**Group member:** Guanyu Ding, Riana Jara, Andrew Chheang



# Event

This synchronization network is a event based network. All operations are triggered by different events.

![diagram](R:/mdImages/diagram.png)



**Event Types:**

|     Event Name      |                         Description                          |
| :-----------------: | :----------------------------------------------------------: |
|     Join Event      | For joining the network, new node will broadcast this event  |
| Join Response Event | After receiving a join event, the node will send back this event |
|     Leave Event     |        Telling all other nodes that a node is leaving        |
|  Remove File Event  |    Telling all other nodes that a certain file is removed    |
|   Send File Event   |                  Send a file to other nodes                  |



For example, Node A is going to send a file to Node B. Then, Node A will create a `sendFileEvent` entity which contains some key information for sending a file such as the path, filename, data of the file, etc. And Node A will send the entire entity to Node B. After Node B receives the entity, it will create the file based on the entity.



