<?php

use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

class ChatController implements MessageComponentInterface {
    private $connections;
    private $users;

    public function __construct() {
        $this->users = new \SplObjectStorage;
        $this->connections = array();
    }

    public function onOpen(ConnectionInterface $conn) {
        // Store the new connection to send messages to later
        $this->users->attach($conn);
        echo "New connection! ({$conn->resourceId})\n";
    }

    public function onMessage(ConnectionInterface $from, $msg) {
        $msgArray = $this->decodeJsonMessage($msg);
        if (!$msgArray) {
            $this->forgetUser($from);
        } else {
            $isNewConnection = $this->rememberUser($from, $msgArray['user_id']);
            if ($isNewConnection) {
                $this->retrieveMessages($from, $msgArray['latitude'], $msgArray['longitude']);
            }

            $this->forwardMessage($from, $msgArray);
            $this->saveMessage($msgArray);
        }
    }

    public function onClose(ConnectionInterface $conn) {
        // The connection is closed, remove it, as we can no longer send it messages
        $this->forgetUser($conn);
        echo "Connection {$conn->resourceId} has disconnected\n";
    }

    public function onError(ConnectionInterface $conn, \Exception $e) {
        echo "An error has occurred: {$e->getMessage()}\n";
		echo "-----------\n {$e->getTraceAsString()}\n-----------\n";
        $this->forgetUser($conn);
    }

    private function rememberUser($conn, $userid) {
        echo "Remembering connection {$conn->resourceId} with user_id {$userid}\n";
        $isNewUser = !array_key_exists($userid, $this->connections);
        if ($isNewUser) {
            $this->connections[$userid] = new \SplObjectStorage;
        }

        $isNewConnection = !$this->connections[$userid]->contains($conn);
        if ($isNewConnection) {
            $this->connections[$userid]->attach($conn);
            $this->users[$conn] = $userid;
        }
        return $isNewConnection;
    }

    private function forgetUser($conn) {
        
		if(!isset($this->users[$conn])) {
			return;
		}
		
		echo "Forgetting connection {$conn->resourceId} with user_id {$this->users[$conn]}\n";
		
		if(isset($this->connections[$this->users[$conn]]) && $this->connections[$this->users[$conn]]->contains($conn)) {
			$this->connections[$this->users[$conn]]->detach($conn);
		}
        
		unset($this->users[$conn]);
		
		if($this->users->contains($conn)) {
			$this->users->detach($conn);
		}
    }

    private function getConnectionsByUserId($userid) {
        echo "Getting active connection for {$userid}\n";
        if (array_key_exists($userid, $this->connections)) {
            echo "Found {$this->connections[$userid]->count()} connections\n";
            return $this->connections[$userid];
        }

        return array();
    }

    private function forwardMessage($from, array $msgArray) {
        $users = $this->retrieveUsers($msgArray['latitude'], $msgArray['longitude']);
        $count = 0;
        foreach ($users as $user) {
            $connections = $this->getConnectionsByUserId($user->user_id);
            foreach ($connections as $connection) {
                if ($connection && $from !== $connection) {
                    // The sender is not the receiver, send to each connection connected
                    echo "Sending message to connection {$connection->resourceId} with user_id {$user->user_id}\n";
                    // var_dump($msgArray);echo "\n";
                    $connection->send(json_encode($msgArray));
                    $count++;
                }
            }
        }

        $from->send(json_encode(array('type' => 'info', 'message' => $count)));
    }

    private function retrieveUsers($lat, $long) {
        echo "Retrieving users nearby ({$lat}, {$long})\n";

        // $users = DB::raw()
        // $users = DB::table('messages')
        //             ->distinct()
        //             ->select('user_id')
        //             ->whereBetween('latitude', array($lat - 1, $lat + 1))
        //             ->whereBetween('longitude', array($long - 1, $long + 1))
        //             ->orderBy('id', 'desc')
        //             ->get();

        $users = DB::select('select *,(6371* acos(cos(radians(?)) * cos(radians(latitude )) * cos(radians(longitude) - radians(?)) 
+ sin(radians(?)) * sin(radians(latitude)))) AS distance FROM messages where latitude = ? and longitude=? having distance<?' , array($lat,$long,$lat,$lat,$long,0.5));
        $userCount = count($users);
        echo "Found {$userCount} users\n";
        // var_dump($users);
        return $users;
    }

    private function retrieveMessages($connection, $lat, $long) {
        echo "Retrieving message for {$connection->resourceId} at ({$lat}, {$long})\n";
        $messages = DB::table('messages')
                    ->whereBetween('latitude', array($lat - 1, $lat + 1))
                    ->whereBetween('longitude', array($long - 1, $long + 1))
                    ->orderBy('id', 'desc')
                    ->take(10)
                    ->get();
        if (($messageCount = count($messages)) > 0) {
            echo "Packing {$messageCount} messages for connection {$connection->resourceId}\n";
            $pastMessages = array();
            for ($i=$messageCount - 1; $i >= 0 ; $i--) { 
                // var_dump($messages[$i]);echo "\n";
                $msg = $messages[$i];
                array_push($pastMessages, array(
                    'user_id' => $msg->user_id,
                    'latitude' => $msg->latitude,
                    'longitude' => $msg->longitude,
                    'message' => $msg->message,
                    'timestamp' =>$msg->created,
                    'image' => ''
                ));
            }

            $json = json_encode(array('type' => 'new', 'messages' => $pastMessages));
            $connection->send($json);
        }
    }

    private function saveMessage(array $msgArray) {
        unset($msgArray['image']);
        unset($msgArray['type']);
        $msgArray['user_id'] = "".$msgArray['user_id']."";
        echo "Saving message:\n";
        var_dump($msgArray);
        DB::table('messages')
            ->insert($msgArray);
    }

    private function decodeJsonMessage($json) {
        $msg = json_decode($json, true);
	if(!isset($msg['latitude'])) return null;
	if(!isset($msg['longitude'])) return null;
	if(!isset($msg['user_id'])) return null;
	if(!isset($msg['message'])) return null;
        return $msg;
    }
}
