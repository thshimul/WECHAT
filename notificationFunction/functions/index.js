'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database
                                    .ref('/Notifications/{user_id}/{notification_id}').onWrite(event =>{
                                          const user_id = event.params.user_id;
                                          const notification =event.params.notification;

                                          console.log('The user id is :',user_id);
                                    });
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
