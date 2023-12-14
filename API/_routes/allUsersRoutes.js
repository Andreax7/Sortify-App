const express = require("express");
const router = express.Router();

const  auth  = require('../_utils/auth')
const users = require('../_database/_queries/allUsers')


router.get('/profile', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
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
        if(userObj){
            const myData = await users.sendRequest(parseInt(userData.userId), req.body).then((res)=> {return res})
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

router.get('/profile/stat', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        if(userObj){
            const myData = await users.myRecycledStat(userData.userId).then((res)=> {return res})
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

router.post('/profile/throw', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        var userData = await users.getUserData(userObj.email).then((res)=> {return res})
        if(userObj){
            const myData = await users.throwTrash(userData.userId, req.body).then((res)=> {return res})
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
            console.log(userObj)
            res.status(201).json(allTypes);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/product/all', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const allProducts = await users.getAllProducts().then((res)=> {return res})
            //console.log(allProducts)
            res.status(201).json(allProducts);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/product/all/:tid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const products = await users.productsByType(parseInt(req.params.tid) ).then((res)=> {return res})
            //console.log(allProducts)
            res.status(201).json(products);
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
            //console.log(allProducts)
            res.status(201).json(product);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })    
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/product/:pid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj){
            const product = await users.getProduct(req.params.pid).then((res)=> {return res})
            //console.log(allProducts)
            res.status(201).json(product);
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
           // console.log(myData)
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