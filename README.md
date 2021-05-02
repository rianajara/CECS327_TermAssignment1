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



# Custom File

The program encapsulate the File class and create a new class named `CustomFile`. It is used to store some key information of the file such as the SHA256, timestamp, path, etc. The SHA256 is a sequence of encryption based on the data of the file. The `equals` method is also override so it will compare the filename, SHA256, and the timestamp, which guarantees that the consistency between local files and the record stored in the program.



![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/123.png)



# Directory

Directory is a data structure to track and store the information of local files and directories. It has two main fields. One is a hash map whose key is the file name and the value is the Custom File type. It is used to save the file under the current directory. Another field is also a hash map whose key is the name of the sub directories under the current directory and the value is the Directory type. Hence, this data structure can easily track all information of local files.

![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/Dir.png)



See the figure above, when creating the Directory entity for the Node directory, it will add files and directory into its two fields. The important things is, when adding the sub directory, it will also create a new Directory entity so the structure will be generated recursively.



# File Controller

File controller is one of the core components of this entire program. It will scan the local files information again and again to make sure the file record in the program is the latest. If it finds the modification of files, it will send the related event to other nodes. For example, if a directory is removed locally, then it will tell other nodes to remove this directory as well.

![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/FC.png)





# Event Handler

Event handler is another core component of this program. It will be created in Node entity and be passed into sender. When a node receives event data from other nodes, the event handler will resolve the data and determine what kind of event arrives. Then it will do different kinds of operations based on the type of the event.

![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/EH.png)



# Example

![](https://images-1259064069.cos.ap-guangzhou.myqcloud.com/images/diagram.png)

For example, Node A is going to send a file to Node B. Node A will create a `sendFileEvent` entity which contains some key information for sending a file such as the path, filename, data of the file, etc. And Node A will send the entire entity to Node B. The receiver of Node B will receive the event data and pass it to the event handler. The event handler will resolve the data and determine the type of the operation. Then, it will take out the data and create the corresponding files on Node B.
