<?php
class MobileModel extends CI_Model {
    function loginUser($username, $password) {
        $sql = "SELECT * FROM users WHERE username=? AND password=?";
        $query = $this->db->query($sql, array($username, $password));
        return $query->result_array();
    }
    
    function registerUser($data) {
        if ($this->db->insert('users', $data)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
    
    function getSingleUser($username){
        $sql = "SELECT * FROM users WHERE username=?";
        $query = $this->db->query($sql, array($username));
        return $query->row_array();
    }
}

?>