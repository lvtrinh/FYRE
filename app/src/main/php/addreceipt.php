<?php
/**
 * This file creates a new receipt. It receives the post parameters for the receipt and
 * uses these parameters to insert a new row into the receipts table.
 */
error_reporting(-1);
ini_set('display_errors', 'On');
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (!empty($_POST["storeName"]) && !empty($_POST["totalPrice"])) {
	// If the field is empty, set it to 'NULL'
	foreach($_POST as $varname => $varvalue) {
		if (empty($varvalue)) {
			$varvalue = null;
		}
		$_POST[$varname] = $varvalue;
	}

    // receiving the post params
    $id = $_POST["id"];
    $storeName = $_POST["storeName"];
    $storeStreet = $_POST["storeStreet"];
    $storeCityState = $_POST["storeCityState"];
    $storePhone = $_POST["storePhone"];
    $storeWebsite = $_POST["storeWebsite"];
    $storeCategory = $_POST["storeCategory"];
    $hereGo = $_POST['hereGo'];
    $cardType = $_POST['cardType'];
    $cardNum = $_POST['cardNum'];
    $paymentMethod = $_POST['paymentMethod'];
    $subtotal = $_POST['subtotal'];
    $tax = $_POST['tax'];
    $totalPrice = $_POST["totalPrice"];
    $date = $_POST['date'];
    $time = $_POST['time'];
    $cashier = $_POST['cashier'];
    $checkNumber = $_POST['checkNumber'];
    $orderNumber = $_POST['orderNumber'];
    $memo = $_POST['memo'];

 	//Create a new receipt
    $receipt = $db->storeReceipt($id, $storeName, $storeStreet, $storeCityState, $storePhone, $storeWebsite, $storeCategory, $hereGo, $cardType, $cardNum, $paymentMethod, $tax, $subtotal, $totalPrice, $date, $time, $cashier, $checkNumber, $orderNumber, $memo);

    if ($receipt) {
    	// Receipt stored succesfully
    	// Populate JSON response array
        $response["error"] = FALSE;
        $response["userID"] = $receipt["id"];
        $response["receipt"]["receiptId"] = $receipt["receipt_id"];
        $response["receipt"]["storeName"] = $receipt["store_name"];
        $response["receipt"]["storeStreet"] = $receipt["store_street"];
        $response["receipt"]["storeCityState"] = $receipt["store_city_state"];
        $response["receipt"]["storePhone"] = $receipt["store_phone"];
        $response["receipt"]["storeWebsite"] = $receipt["store_website"];
        $response["receipt"]["storeCategory"] = $receipt["store_category"];
        $response["receipt"]["hereGo"] = $receipt["here_go"];
        $response["receipt"]["cardType"] = $receipt["card_type"];
    	$response["receipt"]["cardNum"] = $receipt["card_num"];
    	$response["receipt"]["paymentMethod"] = $receipt["payment_method"];
    	$response["receipt"]["subtotal"] = $receipt["subtotal"];
    	$response["receipt"]["tax"] = $receipt["tax"];
    	$response["receipt"]["totalPrice"] = $receipt["total_price"];
    	$response["receipt"]["date"] = $receipt["date"];
    	$response["receipt"]["time"] = $receipt["time"];
    	$response["receipt"]["cashier"] = $receipt["cashier"];
    	$response["receipt"]["checkNumber"] = $receipt["check_number"];
    	$response["receipt"]["orderNumber"] = $receipt["order_number"];
    	$response["receipt"]["memo"] = $receipt["memo"];
        echo json_encode($response);
    } else {
        // user failed to store
        $response["error"] = TRUE;
        $response["error_msg"] = "Unknown error occurred in registration!";
        echo json_encode($response);
        }

} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (storeName or totalPrice) is missing!";
    echo json_encode($response);
}
?>
