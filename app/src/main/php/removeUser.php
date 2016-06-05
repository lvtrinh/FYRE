<?php
/**
 * This file removes a user's account. It receives the post parameter email and uses it
 * to remove the row from the users table associated with that email.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Check if the receipt id is empty
//Must come in as receiptId in post
if (!empty($_POST["email"])) {
	//The user id from POST
	$email = $_POST["email"];

	$update = $db->removeUser($email);

	if ($update) {
		$response["message"] = "The user was succesfully deleted";
		echo json_encode($response);
	} else {
		echo $update;
		$response["error"] = TRUE;
    	$response["error_msg"] = "An error occurred during removal";
    	echo json_encode($response);
	}

} else {
	//The user id was empty
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter email is missing!";
    echo json_encode($response);
}
?>