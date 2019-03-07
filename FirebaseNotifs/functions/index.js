'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const firestore = admin.firestore();
firestore.settings({timestampsInSnapshots: true});

exports.sendNotification = functions.firestore.document("users/{user_id}/Notifications/{notif_id}").onCreate((snap,context) => {

  const from_userid = snap.data().Sender;
  const message = snap.data().Message;
  const userId = context.params.user_id;
  const notifId = context.params.notif_id;
  console.log("*******************************************");

  const from_data = admin.firestore().collection('users').doc(from_userid).get();
  const to_data = admin.firestore().collection('users').doc(userId).get();

  return Promise.all([from_data,to_data]).then((result) => {

    const from_name = result[0].data().name;
    const to_name = result[1].data().name;
    const token_id = result[1].data().token_id;
    console.log(from_name + " sent notification to "+to_name);

    const payload = {
      notification: {
          title : "Notification From : " + from_name,
          body : message,
          icon : "default",
          click_action: "com.example.initish.hackfest.TARGETNOTIFICATION"
      },
      data: {
        message : message,
        from_username : from_name
      }
    };

   return admin.messaging().sendToDevice(token_id, payload).then( result => {
      console.log("Notification Sent. ");
      return result[1]; 
    });  
  });
  

});
