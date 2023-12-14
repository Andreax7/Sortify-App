require('dotenv').config()
const jwt = require('jsonwebtoken');


const authenticateToken = (req, res, next) =>{
    const tkn = req.header("x-access-token");
   // console.log(tkn)
    if (!tkn) return res.status(403).send("Access denied.");
    if (tkn == undefined) res.sendStatus(403);
    else{
        const bearer = tkn.split(' ');
        const token = bearer[1];
        jwt.verify(token, process.env.secretCode, (err, decoded) =>{
            if(err) return res.status(403).send({
                success: false,
                message: 'Failed to authenticate token.'
            });
        req.token = decoded;                     
        }); 
        next();
    } 
}

module.exports = { 
    authenticateToken
 };