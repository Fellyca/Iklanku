<?php

if (isset($status)) {
    if ($status) {
        if (count($data)>0){
            $response = array();
            $response['success'] = 1;
            $response['message'] = "Retrieve Data Success!";
            $response['data'] = $data;
        } else {
            $response = array();
            $response['success'] = 0;
            $response["message"] = "Zero Data!";
        }
    } else {
        $response = array();
        $response['success'] = 0;
        $response['message'] = "Invalid Key";
    }
} else {
    $response = array();
    $response['success'] = 0;
    $response['message'] = "Invalid POST";
}

echo json_encode($response);

?>