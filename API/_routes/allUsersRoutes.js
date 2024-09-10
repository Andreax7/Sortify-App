const express = require("express");
const router = express.Router();

const  auth  = require('../_utils/auth')
const users = require('../_database/_queries/allUsers')
const admin = require('../_database/_queries/admin')
const helpers = require("../_utils/helpers")
const moment = require('moment');

var jwt = require('jsonwebtoken');

router.get('/profile', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
     //   console.log(req)
        if(userObj){
            const myData = await users.getUserData(userObj.email).then((res)=> {return res})
            console.log(userObj)
            res.status(201).json(myData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.post('/profile/update', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
       
        if(userObj){ 
            let allUsers = await admin.AllUsers().then((usr)=> {return usr})
            let checkUsr = allUsers.find( el => {return ((el.email === req.body.email)&&(el.userId !== userObj.uid))} )
            
            if(checkUsr === undefined){
                if(req.body.picture === undefined || req.body.picture === null ){
                    req.body.picture = helpers.toBase64('./image.png')
                } 
              
                const myData = await users.updateUserData(req.body,userObj.uid).then((res)=> {return res})
                if(myData==='User updated'){
                    let updatedUsr = allUsers.find( el => {return el.userId === userObj.uid} )
                    const email  = updatedUsr.email
                    const role = updatedUsr.role
                    const pass = updatedUsr.password
                    const uid = updatedUsr.userId
                    const firstName = updatedUsr.firstName
                    let accessToken = jwt.sign({uid, email, pass, role,firstName}, process.env.secretCode)
                    console.log('UPDATED')
                    return res.status(200).send({"token":accessToken})
                } 
                else{
                    return res.status(400).send("something went wrong")
                }
            }
            else{
                return res.status(400).send("user exists")
            }
           
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.post('/profile/change_password', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
       
        if(userObj){ 
            let allUsers = await admin.AllUsers().then((usr)=> {return usr})
            let checkUsr = allUsers.find( el => {return el.userId === userObj.uid} )
            
            if(checkUsr){
              
                const myData = await users.updatePassword(req.body.password,userObj.uid).then((res)=> {return res})
                if(myData==='password updated'){
                
                    let updatedUsr = allUsers.find( el => {return el.userId === userObj.uid} )
                    const email  = updatedUsr.email
                    const role = updatedUsr.role
                    const pass = updatedUsr.password
                    const uid = updatedUsr.userId
                    const firstName = updatedUsr.firstName
                    let accessToken = jwt.sign({uid, email, pass, role, firstName}, process.env.secretCode)
                    return res.status(200).send({"token":accessToken})
                } 
                else{
                    return res.status(400).send("something went wrong")
                }
            }
            else{
                return res.status(400).send("user doesn't exists")
            }
           
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/profile/requests', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        if(userObj){
            const myData = await users.myRequests(userData.userId).then((res)=> {return res})
           // console.log(myData)
            res.status(201).json(myData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/profile/requests/:rid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const requestData = await users.requestInfo(req.params.rid).then((res)=> {return res})
           // console.log(myData)
            res.status(201).json(requestData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.post('/profile/requests/send', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
       // console.log(req.body)
        if(userObj){
            const timestamp = moment();
            const date = timestamp.format('YYYY-MM-DD')
            const myData = await users.sendRequest(req.body, parseInt(userData.userId), date).then((res)=> {return res})
            var allRequests = await users.myRequests().then((res)=> {return res})
            res.status(201).json(allRequests);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/profile/stat', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        if(userObj){
            const myData = await users.myRecycledStat(userData.userId).then((res)=> {return res})
            res.status(201).json(myData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});
router.get('/profile/all/stats', auth.authenticateToken, async (req, res) =>{
    try{
        
        const userObj = req.token
        if(userObj){
            const myData = await users.AllRecycled().then((res)=> {return res})
           // console.log(myData)
            res.status(201).json(myData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/profile/stats_per_user', auth.authenticateToken, async (req, res) =>{
    try{
        const currentDate = new Date()
        const currentMonth = currentDate.getMonth() + 1
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        if(userObj){
            const allUsers = await admin.AllUsers().then((res)=> {return res})
            const allRecycled = await users.AllRecycled().then((res)=> {return res})
            // Array holds the final result of each user
            const userStats = allUsers.map(user => {
                const userRecycledData = allRecycled.filter(rec => {
                    const [year, month] = rec.date.split("-").map(Number);
                    return rec.userId === user.userId && month === currentMonth;
                    

                })
                const totalQuantity = userRecycledData.reduce((acc, rec) => acc + rec.quantity, 0) // Sum up the quantities
                return totalQuantity
               
            });
            const totalSumOfAllUsers = userStats.reduce((acc, curr) => acc + curr, 0)
            let calc = totalSumOfAllUsers/userStats.length
            calc = parseFloat(calc.toFixed(4))
            res.status(201).json({"total": calc})
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message)
        res.status(400).send('something went wrong!');
    }
});

router.post('/profile/throw', auth.authenticateToken, async (req, res) =>{
    try{
        const timestamp = moment();
        const date = timestamp.format('YYYY-MM-DD');
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        console.log(req.body);
    
        if(userObj){
            const myData = await users.throwTrash(userData.userId, req.body, date).then((res)=> {return res})
           // console.log(myData)
            res.status(201).json(myData);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/type/alltypes', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const allTypes = await users.getAllTrashTypes().then((res)=> {return res})
            //console.log(userObj)
            res.status(201).json(allTypes);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});


/*router.get('/product/all', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            console.log(' getting all products.... \n')
            const allProducts = await users.getAllProducts().then((res)=> {return res})
            console.log(allProducts.length)
            res.status(201).json(allProducts)
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});*/

router.get('/product/all', auth.authenticateToken, async (req, res) => {
    try {
        const userObj = req.token;

        if (userObj) {
          
            const allProducts = await users.getAllProducts();
            
            if (!allProducts || !Array.isArray(allProducts)) {
                throw new Error('Failed to fetch products or invalid response format');
            }

           // console.log(`Total products retrieved: ${allProducts.length} \n `);
            res.status(200).json(allProducts);
        } else {
            res.status(403).json({ "FORBIDDEN": 'Unauthorized' });
        }
    } catch (err) {
        console.error('Error fetching products:', err.message);
        res.status(500).send('Something went wrong!');
    }
});

router.get('/product/all/:tid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const products = await users.productsByType(parseInt(req.params.tid) ).then((res)=> {return res})
            //console.log('filtred ', products)
            res.status(200).json(products);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/product/:barcode', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const product = await users.scanProduct(req.params.barcode).then((res)=> {return res})
           
            if(product === undefined ){
                res.status(404).json({ "NO DATA": 'Product does not exist' }) 
            }else{
                 res.status(201).json(product);
            }
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});


router.get('/product/filter/:pid', auth.authenticateToken, async (req, res) => {
    try {
        const userObj = req.token;
        if (userObj) {
            console.log('--GET PRODUCT BY ID');
            const product = await users.getProductById(parseInt(req.params.pid));
            
            if (product.length === 0) {
                // Product not found
                res.status(404).json({ "error": "Product not found" });
            } else {
                // Product found
              //  console.log('--GET PRODUCT BY ID ', product);
                res.status(200).json(product);
            }
        } else {
            res.status(403).json({ "FORBIDDEN": 'Unauthorized' });
        }
    } catch (err) {
        console.log(err.message);
        res.status(400).send('Something went wrong!');
    }
});

router.get('/locations/all', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            console.log(' (UserRoutes) LOADING ALL LOCATIONS .....')
            const locations = await users.getContainerLocations().then((res)=> {return res})
            res.status(201).json(locations);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/locations/:typeId', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const locations = await users.getContainerLocationsByType(req.params.typeId).then((res)=> {return res})
            console.log('GETTING LOCATIONS ... ', locations)
            res.status(201).json(locations);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});




module.exports = router;