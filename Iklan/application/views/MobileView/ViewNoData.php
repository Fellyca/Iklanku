<?php

if (isset($status)) {
    if ($status){
    $response = array();
    $response['success'] = 1;
    $response['message'] = $message;
    }else {
    $response = array();
    $response['success'] = 0;
    $response['message'] = $message;
    }
} else {
    $response = array();
    $response['success'] = 0;
    $response['message'] = "No POST Data!";
}

echo json_encode($response);

?>