<?php
/**
 * This file updates a receipt's memo. It receives the parameters receipt_id and memo from
 * post and updates the memo field in the row from receipts with receipt_id.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Check if the receipt id is empty
if (!empty($_POST["receipt_id"]) && !empty($_POST["memo"])) {
	//The user new email from POST
	$receipt_id = $_POST["receipt_id"];
	$memo = $_POST["memo"];

	//Update the memo in the database
	$update = $db->changeMemo($receipt_id, $memo);

	if ($update) {
		$response["receipt_id"] = $update["receipt_id"];
		$response["memo"] = $update["memo"];
		echo json_encode($response);
	}

} else {
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter email is missing!";
    echo json_encode($response);
}
?>