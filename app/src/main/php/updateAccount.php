<?php
/**
 * This file updates a users account information. It receives post parameters name, email,
 * and id. It checks whether the email is in valid format and if it is, the correct update
 * function is called according to whether password is filled or not.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

//Must get name, email, and id
if (!empty($_POST["name"]) && !empty($_POST["id"])) {
	//Get the new user parameters from post
		$name = $_POST["name"];
		$email = $_POST["email"];
		$id = $_POST["id"];

	if (filter_var($email, FILTER_VALIDATE_EMAIL)) {

		//Check if the user entered a new password
		if (!empty($_POST["password"])) {
			//Get the password
			$password = $_POST["password"];

			$update = $db->updateAll($name, $email, $password, $id);

			if ($update) {
				$response["id"] = $update["id"];
				$response["name"] = $update["name"];
				$response["email"] = $update["email"];
				echo json_encode($response);
			} else {
				$response["error"] = TRUE;
				$response["error_msg"] = "An error occurred updating name/email/password";
				echo json_encode($response);
			}
		} else {
			//The user did not update their password
			$update = $db->updateBasic($name, $email, $id);

			if ($update) {
				$response["id"] = $update["id"];
				$response["name"] = $update["name"];
				$response["email"] = $update["email"];
				echo json_encode($response);
			} else {
				$response["error"] = TRUE;
				$response["error_msg"] = "An error occurred updating name/email";
				echo json_encode($response);
			}
		}
	} else {
		$response["error"] = TRUE;
    	$response["error_msg"] = "Invalid email!";
    	echo json_encode($response);
	}

} else {
	//The user name was empty
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter name is missing!";
    echo json_encode($response);
}
?>