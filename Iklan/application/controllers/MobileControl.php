<?php

class MobileControl extends CI_Controller {
    function __Construct(){
    parent::__Construct();
    $this->load->Model('MobileModel');
}

function loginUser(){
    $key = $this->input->post('key', TRUE);
    $username = $this->input->post('username', TRUE);
    $password = sha1($this->input->post('password', TRUE));
    
    $status = array();
    if ($key=='iklanaja'){
        $data = $this->MobileModel->loginUser($username, $password);
        if (count($data) > 0){
            $status['status'] = TRUE;
            $status['message'] = "Login Success!";
        }else {
             $status['status'] = FALSE;
             $status['message'] = "Login Failed!";
        }
    }else {
        $status['status'] = FALSE;
        $status['message'] = "Invalied Key";
        }
        $this->load->view('MobileView/ViewNoData', $status);
    }
    
    function registerUser(){
         $key = $this->input->post('key', TRUE);
         $username = $this->input->post('username', TRUE);
         $password = sha1($this->input->post('password', TRUE));
          
          $data = array();
          $data['username'] = $username;
          $data['password'] = $password;
          
           $status = array();
            if ($key=='iklanaja'){
                
                $usernameExists = $this->MobileModel->getSingleUser($username);
                if($usernameExists){
                     $status['status'] = FALSE;
                     $status['message'] = "User already exists!";
                } else {
                    if($this->MobileModel->registerUser($data)) {
                     $status['status'] = TRUE;
                     $status['message'] = "Insert User Success!";
                } else {
                $status['status'] = FALSE;
                $status['message'] = "Insert User Failed!";
                }
            }
               
        }else {
             $status['status'] = FALSE;
              $status['message'] = "Invalid Key!";
        }
        $this->load->view('MobileView/ViewNoData', $status);
    }
}

?>