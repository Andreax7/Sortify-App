require('dotenv').config()
const fs = require('fs')

function validateEmail(email){
    const pattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/
    console.log(email, typeof email, pattern.test(email) )
    return pattern.test(email)
}

function toBase64(filePath) {
    const img = fs.readFileSync(filePath);
    return Buffer.from(img).toString('base64');
  }


module.exports = {
    validateEmail,
    toBase64
};