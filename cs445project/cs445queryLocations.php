<?php

require "cs445conn.php";

$orgid = $_POST["orgid"];

$mysqli_qry ="Select name, description from location where organizationid like '$orgid'";
$result = mysqli_query($conn,$mysqli_qry);
 
if(mysqli_num_rows($result)>0){
    echo "Locations Check Success%";
    while($row = mysqli_fetch_assoc($result)){
        echo $row['name'] . "@" . $row['description'] . ";;;";
    }
}else{
    echo "Failed To Find Locations At " . $orgid;
}

$conn->close();

?>