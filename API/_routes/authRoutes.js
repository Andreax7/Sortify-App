require('dotenv').config()

const express = require("express");
const userModel = require("../_database/_queries/allUsers");
const admin = require("../_database/_queries/admin");
const helpers = require("../_utils/helpers")
var jwt = require('jsonwebtoken');
var bcrypt = require("bcrypt");
const auth  = require('../_utils/auth')

const router = express.Router();



router.post('/signup', async (req, res) =>{
    try{ 
            let allUsers = await admin.AllUsers().then((usr)=> {return usr})
            let checkUsr = allUsers.find( el => {return el.email === req.body.email} )
            
            if(checkUsr === undefined){                    
                if(req.body.picture === undefined || req.body.picture === null ){
                        profilePic = helpers.toBase64('./image.png')
                } 
                else{
                    profilePic = req.body.picture
                }
                if(req.body.firstName === "" || req.body.lastName === ""||  req.body.email === ""){
                    res.statusMessage = "Empty fields!"
                    return res.status(400).send("Empty fields!")
                }
                if(!helpers.validateEmail(req.body.email)){
                    res.statusMessage = "invalid email format"
                    return res.status(400).send("invalid email format")
                }
                const hashPass = bcrypt.hashSync(req.body.password,12)
                const user = await userModel.register(req.body, hashPass, profilePic).then((res)=> {return res});
                //console.log(user) 
                return res.status(201).json("successfully signed in"); // returns users name, email and password
            }
            else{
                
                res.statusMessage= "user exists"
                return res.status(400).send("user exists")
            }
    }
    catch(err){
        console.log(err);
        res.statusMessage= "something went wrong!"
        return res.status(400).send('something went wrong!');
    }

});


router.post('/login', async (req,res)=> {
    try{
        
        var userData = await userModel.getUserData(req.body.email).then((res)=> {return res})
        console.log('LOGIN ')
        if(userData === undefined){
            res.statusMessage = "No user with given email"
            return res.status(400).send('No user with given email')
        }
        var checkPass = bcrypt.compareSync(req.body.password, userData.password)    
        if(checkPass){
				const email  = req.body.email
				const role = userData.role
                const pass = userData.password
                const uid = userData.userId
                const firstName = userData.firstName
				let accessToken = jwt.sign({uid, email, pass, role, firstName}, process.env.secretCode,  { expiresIn: "55m" })
				return res.status(200).send({"token":accessToken})
		}
        else{
            res.statusMessage = "password not matching"
            return res.status(400).send({"error":"password not matching"})
        }
 
    }catch(err){
        console.log(err)
        return res.status(400).send('something went wrong!')
    }
})


module.exports = router;