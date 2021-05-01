# Term Project

**Group member:** Guanyu Ding, Riana Jara, Andrew Chheang



# Event

This synchronization network is an event based network. All operations are triggered by different events.



**Event Types:**

|     Event Name      |                         Description                          |
| :-----------------: | :----------------------------------------------------------: |
|     Join Event      | For joining the network, new node will broadcast this event  |
| Join Response Event | After receiving a join event, the node will send back this event |
|     Leave Event     |        Telling all other nodes that a node is leaving        |
|  Remove File Event  |    Telling all other nodes that a certain file is removed    |
|   Send File Event   |                  Send a file to other nodes                  |





# File Controller

File controller is one of the core components of this entire program. It will scan the local files information again and again to make sure the file record in the program is the latest. If it finds the modification of files, it will send the related event to other nodes. For example, if a directory is removed locally, then it will tell other nodes to remove this directory as well.



# Event Handler

Event handler is another core component of this program. It will be created in Node entity and be passed into sender. When a node receives event data from other nodes, the event handler will resolve the data and determine what kind of event arrives. Then it will do different kinds of operations based on the type of the event.





# Example

![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/diagram (1).png)

For example, Node A is going to send a file to Node B. Node A will create a `sendFileEvent` entity which contains some key information for sending a file such as the path, filename, data of the file, etc. And Node A will send the entire entity to Node B. The receiver of Node B will receive the event data and pass it to the event handler. The event handler will resolve the data and determine the type of the operation. Then, it will take out the data and create the corresponding files on Node B.