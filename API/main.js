var express = require("express")
var bodyParser = require('body-parser')
var cors = require('cors')

require('dotenv').config()
require("./_database/connection.js")

var app = express()
const PORT = process.env.APP_PORT
const androidFrontend =  process.env.ANDROID_FRONTEND


// API ENDPOINTS
const authRoutes = require('./_routes/authRoutes.js')
const userRoutes = require('./_routes/allUsersRoutes.js')
const adminRoutes = require('./_routes/adminRoutes.js')


var corsOptions = {
    origin: "*", // or   origin: androidFrontend
    preflightContinue: false,
    optionsSuccessStatus: 200
  }
app.use(cors(corsOptions));
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

app.use("/auth", authRoutes)
app.use("/admin", adminRoutes)
app.use("/", userRoutes)

//START SERVER
app.listen(PORT, err => {
    if(err) {
      console.log(err); 
      return
    }
    console.log(`Server listening on port: ${PORT}`);
  });

  module.exports = { 
    app
   }
