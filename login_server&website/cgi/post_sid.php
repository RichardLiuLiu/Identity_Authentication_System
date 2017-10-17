<?php

function request_by_curl($remote_server, $post_string) {
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $remote_server);
	curl_setopt($ch, CURLOPT_POSTFIELDS, $post_string);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	//curl_setopt($ch, CURLOPT_USERAGENT, "snsgou.com's CURL Example beta");
	$data = curl_exec($ch);
	curl_close($ch);
	return $data;
}

if(isset($_REQUEST["ssid"])) $ssid = $_REQUEST["ssid"];
else{
    echo '{"imsi":"timeout",';
    echo '"remote_ip":"'.$_SERVER['REMOTE_ADDR'].'"}';
    exit;
}
$post_data = "TEMPESTp".$ssid;
request_by_curl($_SERVER['REMOTE_ADDR'].':2016', $post_data);
/*
$counter = 20;
while(!file_exists($ssid.".txt") and $counter-->0)
{
    sleep(1);
}
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
*/
?>
