<?php
/**
 * This file creates a new user account. It receives the post parameters name, email,
 * password, security_question, and security_answer. It uses these parameters to
 * insert a new row into the users table.
 */
ini_set('display_errors', 1);
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['security_question']) && isset($_POST['security_answer'])) {

    // receiving the post params
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $security_question = $_POST['security_question'];
    $security_answer = $_POST['security_answer'];

 	// check if the email entered is a valid email
 	if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
 		$response["error"] = TRUE;
        $response["error_msg"] = "Email is invalid!";
        echo json_encode($response);
 	}

    // check if user is already existed with the same email
    else if ($db->isUserExisted($email)) {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "User already existed with " . $email;
        echo json_encode($response);
    } else {
        // create a new user
        $user = $db->storeUser($name, $email, $password, $security_question, $security_answer);
        if ($user) {
            // user stored successfully
            $response["error"] = FALSE;
            $response["id"] = $user["id"];
            $response["uid"] = $user["unique_id"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (name, email or password) is missing or invalid!";
    echo json_encode($response);
}
?>
