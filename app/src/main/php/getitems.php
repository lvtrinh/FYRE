<?php
/**
 * This file retrieves all the items for a receipt. It receives the post parameter receiptId
 * and uses it to retrieve all the items associated with receiptId.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Check if the receipt id is empty
//Must come in as receiptId in post
if (!empty($_POST["receiptId"])) {
	//The user id from POST
	$receiptId = $_POST["receiptId"];

	//getReceipts returns an array holding all the receipts for the user with id=$id.
	//Each receipt is also an array
	$items = $db->getReceiptItems($receiptId);

	if ($items) {
		$response["items"] = $items;
		echo json_encode($response);
	}

} else {
	//The user id was empty. Can't retrieve receipts.
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter receipt ID is missing!";
    echo json_encode($response);
}
?>