<?php

use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

class ChatController implements MessageComponentInterface {
    protected $clients;
    private $currentConn;

    public function __construct() {
        $this->clients = new \SplObjectStorage;
        $this->currentConn = array();
    }

    public function onOpen(ConnectionInterface $conn) {
        // Store the new connection to send messages to later
        $this->clients->attach($conn);
        echo "New connection! ({$conn->resourceId})\n";
    }

    public function onMessage(ConnectionInterface $from, $msg) {
        $msgArray = $this->decodeJsonMessage($msg);
        if (!$msgArray) {
            $this->forgetUser($from);
        } else {
            $isNewUser = $this->rememberUser($from, $msgArray);
            if ($isNewUser) {
                $this->retrieveMessages($from, $msgArray['latitude'], $msgArray['longitude']);
            }

            $this->forwardMessage($from, $msgArray);
            $this->saveMessage($msgArray);
        }
    }

    public function onClose(ConnectionInterface $conn) {
        // The connection is closed, remove it, as we can no longer send it messages
        $this->clients->detach($conn);

        echo "Connection {$conn->resourceId} has disconnected\n";
    }

    public function onError(ConnectionInterface $conn, \Exception $e) {
        echo "An error has occurred: {$e->getMessage()}\n";

        $conn->close();
    }

    public function rememberUser($from, array $msgArray) {
        if (!array_key_exists($msgArray['user_id'], $this->currentConn)) {
            echo "Remembering client {$from->resourceId} with user_id {$msgArray['user_id']}\n";
            $this->currentConn[$msgArray['user_id']] = $from;
            return true;
        }

        return false;
    }

    public function forgetUser($client) {
        $this->clients->detach($client);
    }

    public function getClientByUserId($userid) {
        echo "Getting active client for {$userid}\n";
        if (array_key_exists($userid, $this->currentConn)) {
            echo "Found client {$this->currentConn[$userid]->resourceId}\n";
            return $this->currentConn[$userid];
        }

        return null;
    }

    public function forwardMessage($from, array $msgArray) {
        $users = $this->retrieveUsers($msgArray['latitude'], $msgArray['longitude']);
        $count = 0;
        foreach ($users as $user) {
            $client = $this->getClientByUserId($user->user_id);
            if ($client && $from !== $client) {
                // The sender is not the receiver, send to each client connected
                echo "Sending message to client {$client->resourceId} with user_id {$user->user_id}\n";
                // var_dump($msgArray);echo "\n";
                $client->send(json_encode($msgArray));
                $count++;
            }
        }

        $from->send(json_encode(array('type' => 'info', 'message' => $count)));
    }

    public function retrieveUsers($lat, $long) {
        echo "Retrieving users nearby ({$lat}, {$long})\n";
        $users = DB::table('messages')
                    ->distinct()
                    ->select('user_id')
                    ->whereBetween('latitude', array($lat - 1, $lat + 1))
                    ->whereBetween('longitude', array($long - 1, $long + 1))
                    ->orderBy('id', 'desc')
                    ->get();

        $userCount = count($users);
        echo "Found {$userCount} users\n";
        // var_dump($users);
        return $users;
    }

    public function retrieveMessages($client, $lat, $long) {
        echo "Retrieving message for {$client->resourceId} at ({$lat}, {$long})\n";
        $messages = DB::table('messages')
                    ->whereBetween('latitude', array($lat - 1, $lat + 1))
                    ->whereBetween('longitude', array($long - 1, $long + 1))
                    ->orderBy('id', 'desc')
                    ->take(10)
                    ->get();
        if (($messageCount = count($messages)) > 0) {
            echo "Sending {$messageCount} messages to client {$client->resourceId}\n";
            for ($i=$messageCount - 1; $i >= 0 ; $i--) { 
                // var_dump($messages[$i]);echo "\n";
                $msg = $messages[$i];
                $json = json_encode(array(
                    'user_id' => $msg->user_id,
                    'latitude' => $msg->latitude,
                    'longitude' => $msg->longitude,
                    'message' => $msg->message,
                    'timestamp' =>$msg->created,
                    'image' => ''
                ));
                $client->send($json);
            }
        }
    }

    public function saveMessage(array $msgArray) {
        unset($msgArray['image']);
        unset($msgArray['type']);
        $msgArray['user_id'] = "".$msgArray['user_id']."";
        echo "Saving message:\n";
        var_dump($msgArray);
        DB::table('messages')
            ->insert($msgArray);
    }

    public function decodeJsonMessage($json) {
        $msg = json_decode($json, true);
        return $msg;
    }
}