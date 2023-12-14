require('dotenv').config()

const express = require("express");
const userModel = require("../_database/_queries/allUsers");
const admin = require("../_database/_queries/admin");
const helpers = require("../_utils/helpers")
var jwt = require('jsonwebtoken');
var bcrypt = require("bcrypt");


const router = express.Router();



router.post('/signup', async (req, res) =>{
    try{ 
            let allUsers = await admin.AllUsers().then((res)=> {return res})
            let usr = allUsers.find( el => el.email === req.body.email )
            if(usr === undefined){                    
                if(req.body.picture === undefined || req.body.picture === null ){
                        profilePic = helpers.toBase64('./image.png')
                } 
                else{
                    profilePic = req.body.picture
                }
                if(req.body.firstName==="" || req.body.lastName === ""||  req.body.email === ""){
                    return res.status(400).send('Empty fields!')
                }
                if(!helpers.validateEmail(req.body.email)){
                    return res.status(400).send('invalid email format')
                }
                const hashPass = bcrypt.hashSync(req.body.password,12)
                const user = await userModel.register(req.body, hashPass, profilePic).then((res)=> {return res});
                //console.log(user) 
                return res.status(201).json(user); // returns users name, email and password
            }
            else{
                return res.status(400).send('user exists')
            }
    }
    catch(err){
        console.log(err);
        return res.status(400).send('something went wrong!');
    }

});

router.post('/login', async (req,res)=> {
    try{
        var userData = await userModel.getUserData(req.body.email).then((res)=> {return res})
        if(userData === undefined){
            return res.status(400).send('No user with given email');
        }
        var checkPass = bcrypt.compareSync(req.body.password, userData.password);    
        if(checkPass){
				const email  = req.body.email
				const role = userData.role
                const pass = userData.password
				accessToken = jwt.sign({email, pass, role}, process.env.secretCode);
				return res.status(200).send({"token":accessToken});
		}
        else{
            return res.status(400).send({"error":"password not matching"})
        }
 
    }catch(err){
        console.log(err);
        return res.status(400).send('something went wrong!');
    }
})

//LOGOUT


module.exports = router;