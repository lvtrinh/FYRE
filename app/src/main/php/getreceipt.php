<?php
/**
 * This file gets all the receipts for a user. It receives the post parameter id and uses
 * it to retrieve all receipts associated with that id.
 */

error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Check if the user id is empty
if (!empty($_POST["id"])) {
	//The user id from POST
	$id = $_POST["id"];

	//getReceipts returns an array holding all the receipts for the user with id=$id.
	//Each receipt is also an array
	$receipts = $db->getReceipts($id);

	if ($receipts) {
		$response["receipts"] = $receipts;
		echo json_encode($response);
	}

} else {
	//The user id was empty. Can't retrieve receipts.
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter user ID is missing!";
    echo json_encode($response);
}
?>