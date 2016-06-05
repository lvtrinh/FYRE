<?php
/**
 * This file updates a users security_question and security_answer. It receives the
 * post parameters id, security_question, and security_answer and uses them to update the
 * fields security_question and security_answer in the row from the users table with id.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();


// json response array
$response = array("error" => FALSE);

//Make sure the user input the correct fields
if (!empty($_POST["id"]) && !empty($_POST["security_question"]) && !empty($_POST["security_answer"])) {
	$id = $_POST["id"];
	$security_question = $_POST["security_question"];
	$security_answer = $_POST["security_answer"];

	//Update the users security question and answer
	$update = $db->changeSecurityPreferences($id, $security_question, $security_answer);

	if ($update) {
		$response["id"] = $update["id"];
		echo json_encode($response);
	}

} else {
	//User did not enter at least one of the fields
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (security question or security answer) is missing!";
    echo json_encode($response);
}
?>