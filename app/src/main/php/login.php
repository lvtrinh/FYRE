<?php
/**
 * This file receives the post parameters email and password. It uses these parameters to
 * check whether the email exists in the database and validates the email/password
 * combination for login.
 */
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['password'])) {

    // receiving the post params
    $email = $_POST['email'];
    $password = $_POST['password'];

    if ($db->isUserExisted($email)) {

    	// get the user by email and password
    	$user = $db->getUserByEmailAndPassword($email, $password);

    	if ($user != false) {
    	    // use is found
    	    $response["error"] = FALSE;
    	    $response["id"] = $user["id"];
    	    $response["uid"] = $user["unique_id"];
    	    $response["user"]["name"] = $user["name"];
    	    $response["user"]["email"] = $user["email"];
    	    $response["user"]["created_at"] = $user["created_at"];
    	    $response["user"]["updated_at"] = $user["updated_at"];
    	    echo json_encode($response);
    	} else {
    	    // user is not found with the credentials
    	    $response["error"] = TRUE;
    	    $response["error_msg"] = "Login credentials are wrong. Please try again!";
    	    echo json_encode($response);
    	}
    } else {
    	$response["error"] = TRUE;
    	$response["error_msg"] = "Email is invalid.";
    	echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
?>
