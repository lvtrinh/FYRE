<?php
/**
 * This file validates a users security question and security answer. It receives post
 * parameters email and security_answer. If the security_answer associated with email in
 * the users table matches the security_answer from post, then the security_question and
 * security_answer are validated.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Check if the receipt id is empty
//Must come in as receiptId in post
if (!empty($_POST["email"]) && !empty($_POST["security_answer"])) {
	//The user email from POST
	$email = $_POST["email"];
	$security_answer = $_POST["security_answer"];

	$details = $db->getSecurityDetails($email);

	if ($details) {
		$question = $details["security_question"];
		$answer = $details["security_answer"];

		if ($security_answer == $answer) {
			$response["validated"] = TRUE;
			$response["email"] = $email;
			echo json_encode($response);
		} else {
			$response["validated"] = FALSE;
			$response["error_msg"] = "Answer is incorrect.";
			echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
    	$response["error_msg"] = "An error occured validating the security question.";
    	echo json_encode($response);
	}

} else {
	//The user id was empty. Can't retrieve receipts.
	$response["error"] = TRUE;
    $response["error_msg"] = "Please answer the security question.";
    echo json_encode($response);
}
?>