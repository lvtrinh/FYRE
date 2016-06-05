<?php
/**
 * This file sends a temporary password to a users email. It receives the post parameter
 * email and uses it to change the user's password in the row associated with email. Then,
 * using PHPMailer, an email is sent to the post email containing the temporary password.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
require '/var/www/PHPMailer-master/PHPMailerAutoload.php';
$db = new DB_Functions();
date_default_timezone_set('Etc/UTC');


// json response array
$response = array("error" => FALSE);

//Check is the email is empy
if (!empty($_POST["email"])) {
	//The user email from POST
	$email = $_POST["email"];

	$rand = substr(md5(rand()), 0, 7);

	$update = $db->changePassword($email, $rand);

	if ($update) {
		$address = $update["email"];
		$name = $update["name"];

		$message = "<p>Hi ".$name.",</p>";
		$message .= "<p>Your temporary password is: ".$rand."<br>Please login and reset your password immediately.</p>";
		$message .= "<p>Sincerely,</p>";
		$message .= "<p>Team FYRE</p>";

		$body = "<html>\n";
    	$body .= "<body style=\"font-family:Verdana, Verdana, Geneva, sans-serif; font-size:12px; color:#666666;\">\n";
    	$body = $message;
    	$body .= "</body>\n";
    	$body .= "</html>\n";

		//Create a new PHPMailer instance
		$mail = new PHPMailer();
		$mail->isSMTP();
		$mail->SMTPDebug = 2;
		$mail->Debugoutput = 'html';
		$mail->Host = 'smtp.gmail.com';
		$mail->Port = 587;
		$mail->SMTPSecure = 'tls';
		$mail->SMTPAuth = true;
		$mail->Username = "teamfyre110@gmail.com";
		$mail->Password = "20Fyre@09";
		$mail->setFrom('teamfyre10@gmail.com');
		$mail->addAddress("$address", "$name");
		$mail->Subject = 'FYRE Password Reset';
		$mail->AltBody = 'This is a plain-text message body';
		$mail->Body = $body;
		$mail->isHTML(true);

		//send the message, check for errors
		if (!$mail->send()) {
			$response["pw"] = $rand;
			$response["mail_error"] = True;
			$response["mail_error_message"] = "Mailer Error: " . $mail->ErrorInfo;
			echo json_encode($response);
		} else {
			$response["pw"] = $rand;
			$repsonse["mail_error"] = False;
			$response["mail_error_message"] = "Message sent!";
			echo json_encode($response);
		}
	} else {
		//Change password failed
		$response["pw"] = $rand;
		$response["error"] = TRUE;
    	$response["error_msg"] = "There was an error updating password";
    	echo json_encode($response);
	}

} else {
	//The user id was empty. Can't retrieve receipts.
	$response["error"] = TRUE;
    $response["error_msg"] = "Required parameter email is missing!";
    echo json_encode($response);
}
?>