<?php
/**
 * This file validates a user's email and returns their security question. It receives the
 * post parameter email and if the email exists in the users table, the security question
 * associated with that row is returned.
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
	//The user email from POST
	$email = $_POST["email"];

	if ($db->isUserExisted($email)) {
		//User exists. Now retrieve their security question
		$question = $db->getSecurityQuestion($email);

		if ($question) {
			$response["email"] = $question["email"];
			$response["security_question"] = $question["security_question"];
			echo json_encode($response);
		}
	} else {
		//The user id was empty. Can't retrieve receipts.
		$response["error"] = TRUE;
    	$response["error_msg"] = "User email invalid!";
    	echo json_encode($response);
	}

} else {
	//The user id was empty. Can't retrieve receipts.
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter email is missing!";
    echo json_encode($response);
}
?>