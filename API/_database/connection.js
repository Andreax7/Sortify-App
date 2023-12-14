const sqlite3 = require('sqlite3').verbose();
const fs = require("fs");
var path = require("path");

const DBpath = "D:/Docs/SPEC/Diplomski/API/_database/data.db"


    const db = new sqlite3.Database(DBpath, sqlite3.OPEN_READWRITE, (err) => { 
        if(err) 
            { 
                console.log("Error Occurred - " + err.message); 
                throw err
            }
        });



module.exports =  db ;
  