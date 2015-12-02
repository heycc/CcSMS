package com.heycc.ccsms;

/**
 * Created by cc on 11/26/15.
 */
public class Conversation {
    public String title;
    public String time;
    public String msg;

    public Conversation(String title, String msg, String time) {
        this.title = title;
        this.time = time;
        this.msg = msg;
    }
}

/* SMS cursor columns
_id
thread_id
association_id
address
person
date
date_sent
protocol
read
status
type
reply_path_present
subject
body
service_center
locked
error_code
report_date
port
seen
sync_ver
uuid
group_msg_id
imsi
is_favorite
sim_id
 */
