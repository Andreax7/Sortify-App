const express = require("express");

const auth  = require('../_utils/auth')
const admin = require('../_database/_queries/admin')
const users = require('../_database/_queries/allUsers')
const helpers = require('../_utils/helpers')
const router = express.Router();

router.post('/type/add', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            if(req.body.typeName === undefined || req.body.info === undefined || req.body.typeName === "" || req.body.info === "" ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            const allTypes = await users.getAllTrashTypes().then((res)=> {return res})
            let name = allTypes.find( el => el.typeName === req.body.typeName )
            if(name === undefined){
                DBresponse = await admin.addNewType(req.body).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This type exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })     
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.delete("/type/:id", auth.authenticateToken, async (req, res) => {
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allTypes = await users.getAllTrashTypes().then((res)=> {return res})
            let id = allTypes.find( el => el.typeId === parseInt(req.params.id) )
            if(id !== undefined){
                DBresponse = await admin.removeTrashType(req.params.id).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This type does not exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put("/type/:id", auth.authenticateToken, async (req, res) => {
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allTypes = await users.getAllTrashTypes().then((res)=> {return res})
            let id = allTypes.find( el => el.typeId === parseInt(req.params.id) )
            if(id !== undefined){
                if(req.body.typeName === "" || req.body.info === "" ){
                    return res.status(400).json({ "error": 'Empty fields' })
                }
                DBresponse = await admin.updateTrashType(req.body, req.params.id).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This type does not exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }catch (err){
        console.log(err.message);
        res.status(400).send(err.message);
    }
       
});

router.post('/product/add', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        //console.log(userObj)
        if(userObj.role === 1){
            if(req.body.barcode === undefined || req.body.productName === undefined || req.body.details === undefined || req.body.typeId === undefined ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            else if(req.body.barcode === "" || req.body.productName === "" || req.body.details === "" || req.body.typeId === "" ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            const allProducts = await users.getAllProducts().then((res)=> {return res})
            let name = allProducts.find( el => el.productName === req.body.productName )
            if(name === undefined){
                DBresponse = await admin.addNewProduct(req.body).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This product exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })     
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put("/product/:id", auth.authenticateToken, async (req, res) => {
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allProducts = await users.getAllProducts().then((res)=> {return res})
            let id = allProducts.find( el => el.productId === parseInt(req.params.id) )
      
            if(id !== undefined){
                if(req.body.productName === "" || req.body.details === "" || req.body.barcode === ""  ){
                    return res.status(400).json({ "error": 'Empty fields' })
                }
                DBresponse = await admin.updateProduct(req.body, req.params.id).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This type does not exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }catch (err){
        console.log(err.message);
        res.status(400).send(err.message);
    }
       
});

router.delete("/product/:id", auth.authenticateToken, async (req, res) => {
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allProducts = await users.getAllProducts().then((res)=> {return res})
            let id = allProducts.find( el => el.productId === parseInt(req.params.id) )
            if(id !== undefined){
                DBresponse = await admin.removeProduct(req.params.id).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This product does not exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});


router.get('/users', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allUsers = await admin.AllUsers().then((res)=> {return res})
           // console.log(allUsers)
            res.status(201).json(allUsers);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put('/users/:uid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const users = await admin.AllUsers().then((res)=> {return res})
            const user = users.find( el => el.userId === parseInt(req.params.uid) )
            console.log(user)
            if(user === undefined){
                return res.status(400).json("unknown userId");
            }
            else{
                if(req.body.role){
                    const DBresponse = await admin.setUserAdmin(parseInt(req.body.role),parseInt(req.params.uid)).then((res)=> {return res})
                    return res.status(201).json(DBresponse)
                }
                if(req.body.active){
                    const DBresponse = await admin.changeProfileActivity(parseInt(req.body.active),parseInt(req.params.uid)).then((res)=> {return res})
                    return res.status(201).json(DBresponse)
                }
                    
            }
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/users/requests', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const userRequests = await admin.AllUserRequests().then((res)=> {return res})
            console.log(userRequests)
       // await user.save();
            res.status(201).json(userRequests);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});


router.get('/users/requests/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const userRequests = await admin.userReq(req.params.id).then((res)=> {return res})
            console.log(userRequests)
            res.status(201).json(userRequests);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put('/users/requests/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            if(req.body.seen !== undefined || req.body.seen !== "" ){
                const userRequests = await admin.updateUserRequest(parseInt(req.body.seen), req.params.id).then((res)=> {return res})
                console.log(userRequests)
                res.status(201).json(userRequests);
            }
            else res.status(403).json({ "Error": 'seen parameter not given' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});


router.get('/containers', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const allContainers = await admin.AllContainerLocations().then((res)=> {return res})
            console.log(allContainers)
       // await user.save();
            res.status(201).json(allContainers);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/containers/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const container = await admin.getContainer(parseInt(req.params.id)).then((res)=> {return res})
            console.log(container)
       // await user.save();
            res.status(201).json(container);
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.post('/locations/add', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        //console.log(userObj)
        if(userObj.role === 1){
            if(req.body.typeId === undefined || req.body.location === undefined || req.body.active === undefined ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            else if(req.body.barcode === "" || req.body.productName === "" || req.body.details === "" || req.body.typeId === "" ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            const locations = await admin.AllContainerLocations().then((res)=> {return res})
            let name = locations.find( el => el.productName === req.body.productName )
            if(name === undefined){
                DBresponse =  await admin.addContainer(body).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This product exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })     
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put('/containers/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            if(req.body.typeId !== undefined || req.body.typeId !== "" ){
                const container = await admin.updateContainer(body,parseInt(req.params.id)).then((res)=> {return res})
                console.log(container)
                res.status(201).json(container);
            }
            else res.status(403).json({ "Error": 'seen parameter not given' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put('/containers/deactivate/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            if(req.body.typeId !== undefined || req.body.typeId !== "" ){
                const container = await admin.getContainer(parseInt(req.params.id))
                let findCount = container.find( el => el.containerId === parseInt(req.params.id) )
                if(findCount === undefined){
                    res.status(403).json({ "Error": 'container does not exist' })
                }
                else{
                    const deactivate = await admin.deactivateContainer(parseInt(req.params.id)).then((res)=> {return res})
                    console.log(deactivate)
                    res.status(201).json(deactivate);
                }
                
            }
            else res.status(403).json({ "Error": 'seen parameter not given' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.post('/occupancy/add', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        //console.log(userObj)
        if(userObj.role === 1){
            if(req.body.date === undefined || req.body.state === undefined || req.body.containerId === undefined ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            else if(req.body.date === "" || req.body.state === "" || req.body.containerId === "" ) {
                return res.status(400).json({ "error": 'Empty fields' }) 
            }
            const occupancies = await admin.getAllOccupancies().then((res)=> {return res})
            let id = occupancies.find( el => el.containerId === req.body.containerId )
            if(id === undefined){
                DBresponse =  await admin.addOccupancy(parseInt(req.body.containerId),parseInt(req.body.state), req.body.date ).then((res)=> {return res})
                return res.status(201).json(DBresponse)
            }
            else res.status(400).json({ "error": 'This product exists' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })     
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.get('/occupancy/container/:cid', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            const occupancies = await admin.getAllOccupancies().then((res)=> {return res})
            let name = occupancies.find( el => el.containerId === parseInt(req.params.cid) )
            if(name === undefined){
                    res.status(403).json({ "Error": 'container does not exist' })
                }
                else{
                    const contOccupancy = await admin.getContainerOccupancy(parseInt(req.params.cid)).then((res)=> {return res})
                    console.log(contOccupancy)
                    res.status(201).json(contOccupancy);
                }
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

router.put('/occupancy/:id', auth.authenticateToken, async (req, res) =>{
    try{
        const userObj = req.token
        if(userObj.role === 1){
            if(helpers.checkDateFormat(req.body.date)){
                if(req.body.state !== undefined || req.body.containerId !== undefined){
                        const container = await admin.getContainerOccupancy(parseInt(req.body.containerId)).then((res)=> {return res})
                        let findCount = container.find( el => el.ocId === parseInt(req.params.id) )
                        if(findCount === undefined){
                                res.status(403).json({ "Error": 'container does not exist' })
                        }
                        else{
                                const change = await admin.updateContainerOccupancy(parseInt(req.params.id), parseInt(req.body.state), req.body.date).then((res)=> {return res})
                                console.log(change)
                                res.status(201).json(change);
                        }
                            
                }
            }
            else res.status(403).json({ "Error": 'wrong date parameter given' })
        }
        else res.status(403).json({ "FORBIDDEN": 'Unauthorized' })
    }
    catch (err){
        console.log(err.message);
        res.status(400).send('something went wrong!');
    }
});

module.exports = router;
