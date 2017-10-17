<?php
if(isset($_REQUEST["ssid"])) $ssid = $_REQUEST["ssid"];
else exit;
if(file_exists($ssid.".txt"))
{
    $fid = fopen($ssid.".txt","r");
    $data = fgets($fid,999);
    fclose($fid);
    unlink($ssid.".txt");
    echo $data;
}
else
{
    echo '{"imsi":"timeout"}';
}
?>