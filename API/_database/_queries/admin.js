const db = require('../connection.js')


function getProductByName(pName) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`SELECT * FROM products 
            WHERE productName = ?`,
      [parseInt(pName)],
      (err, rows) => {
          if (err) reject(err)
          result.push(rows)
      },
      () => { resolve(result) }
    );
  }) 
}
function addNewProduct(body){
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`INSERT INTO products (barcode, productName, image, details, typeId, confirmed) VALUES (?, ?, ?, ?, ?, ?);`,
          [body.barcode, body.productName, body.image, body.details, body.typeId, 1], 
          (err, rows) => {
                          if(err) { reject(err) }
                          result.push(rows)
            },
          () => { resolve(body.productName + " successfully added") }
    )
  })
}

function removeProduct(productId){
  return new Promise((resolve, reject)=> {
    db.run(`DELETE FROM products WHERE productId = ?`,
        parseInt(productId),
        (err, result) => {
          console.log(result,err)
            if (err) {
                reject(err)
            }
            resolve("Successfully deleted")
        }
    )
  })
}

function updateProduct(body, id){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE products 
              SET productName = ?, confirmed = ?, typeId = ?, details = ?, barcode = ?
              WHERE productId = ?`,
      [body.productName, body.confirmed, parseInt(body.typeId), body.details, body.barcode, parseInt(id)],
      (err, result) => {
          if (err) reject(err)
          resolve(body.productName +" successfully updated")
      }
    );
  })
}

function addNewType(body){
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`INSERT INTO types (typeName, info) VALUES (?, ?);`,
          [body.typeName, body.info], 
          (err, rows) => {
            console.log(rows)
                if(err) { reject(err) }
                result.push(rows)
            },
          () => { resolve(body.typeName + " successfully added") }
    )
  })
}

function removeTrashType(typeId){
  return new Promise((resolve, reject)=> {
    db.run(`DELETE FROM types WHERE typeId = ?`,
        typeId,
        (err, result) => {
            if (err) {
                reject(err)
            }
            resolve("Successfully deleted")
        }
    )
  })
}

function updateTrashType(body, id){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE types 
            SET typeName = ?, info = ?
            WHERE typeId = ?`,
            [body.typeName, body.info, parseInt(id)],
            function (err, result) {
                if (err) reject(err)
                resolve(body.typeName +" successfully updated")
              }
    );
  })
}






function AllUsers(){
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM users;`, (err, rows) => {
      if(err) { reject(err) }
      result.push(rows)
    }, () => {
      resolve(result)
    })
  })
}

function setUserAdmin(role, uid){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE users 
            SET role = ?
            WHERE userId = ?`,
      [role, parseInt(uid)],
      (err, result) => {
          if (err) reject(err)
          resolve(uid + " user successfully updated")
        }
    );
  })
}

function changeProfileActivity (active, uid){ 
  return new Promise((resolve, reject) => {
    db.run(`UPDATE users 
            SET active = ?
            WHERE userId = ?`,
    [active, parseInt(uid)],
    (err, result) => {
        if (err) reject(err)
        resolve(uid + " user successfully updated")
      }
    );
  })
}



function AllUserRequests(){
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM forms;`, (err, rows) => {
      if(err) { reject(err) }
      result.push(rows) 
    }, () => {
      resolve(result)
    })
  })
}

function userReq(fid){
  return new Promise((resolve, reject) => {
    db.run(`SELECT * FROM forms 
            WHERE formId = ?`,
      [parseInt(fid)],
      (err, result) => {
          if (err) reject(err)
          resolve(body.productName +"successfully updated")
      }
    );
  })
}
function updateUserRequest(seen,fid) {
  return new Promise((resolve, reject) => {
    db.run(`UPDATE forms 
            SET seen = ?
            WHERE formId = ?`,
    [seen, parseInt(fid)],
    (err, result) => {
        if (err) reject(err)
        resolve(fid + " user successfully updated")
      }
    );
  })
}

function addContainer(body){
    return new Promise((resolve, reject) => {
      let result = []
      db.run(`INSERT INTO containers (location, active, typeId) VALUES (?, ?, ?);`,
            [body.location, 1, body.typeId], 
            (err, rows) => {
              console.log(rows)
                  if(err) { reject(err) }
                  result.push(rows)
              },
            () => { resolve("container " + body.location + " successfully added") }
      )
    })
}

function AllContainerLocations() {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM containers ;`, (err, rows) => {
      if(err){ reject(err) }
      result.push(rows) 
    }, () => {
      resolve(result)
    })
  }) 
}

function updateContainer(body,cid) {
  return new Promise((resolve, reject) => {
    db.run(`UPDATE containers 
            SET active = ?, location = ?, typeId = ?
            WHERE containerId = ?`,
    [parseInt(body.active), body.location, parseInt(body.typeId), parseInt(cid)],
    (err, result) => {
        if (err) reject(err)
        resolve(fid + " user successfully updated")
      }
    );
  })
}

function deactivateContainer(cid){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE containers 
            SET active = ?
            WHERE containerId = ?`,
    [0, parseInt(cid)],
    (err, result) => {
        if (err) reject(err)
        resolve(fid + " user successfully updated")
      }
    );
  })
}

function addOccupancy(cid, state, date){
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`INSERT INTO occupancy (containerId, state, date) VALUES (?, ?, ?);`,
          [cid, state, date], 
          (err, rows) => {
            console.log(rows)
                if(err) { reject(err) }
                result.push(rows)
            },
          () => { resolve("container has " + state + " occupancy") }
    )
  })
}

function getAllOccupancies(){
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM occupancy ;`,
            (err, rows) => {
              if(err){ reject(err) }
              result.push(rows) 
            }, () => {
              resolve(result)
    })
  })  
}

function getContainerOccupancy(cid){
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM occupancy 
            WHERE containerId=? ;`, [cid],
            (err, rows) => {
              if(err){ reject(err) }
              result.push(rows) 
            }, () => {
              resolve(result)
    })
  })  
}

function updateContainerOccupancy(oid, cid, state, date){
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`UPDATE occupancy 
            SET date = ?, state = ? WHERE ocId = ?;`,
          [cid, date, state, oid], 
          (err, rows) => {
            console.log(rows)
                if(err) { reject(err) }
                result.push(rows)
            },
          () => { resolve("container "+ cid +" has " + state + " occupancy") }
    )
  })
}

function getContainer(cid){
    return new Promise((resolve, reject) => {
      let result = []
      db.all(`SELECT * FROM containers 
              WHERE containerId=? ;`, [cid],
              (err, rows) => {
                if(err){ reject(err) }
                result.push(rows) 
              }, () => {
                resolve(result)
      })
    })  
}

module.exports = {
  getProductByName,
  addNewProduct,
  removeProduct,
  updateProduct,
  addNewType,
  removeTrashType,
  updateTrashType, 
  AllUsers,
  setUserAdmin,
  changeProfileActivity,
  AllUserRequests,
  userReq,
  updateUserRequest,
  addContainer,
  AllContainerLocations,
  getContainer,
  updateContainer,
  deactivateContainer,
  addOccupancy,
  getContainerOccupancy,
  updateContainerOccupancy,
  getAllOccupancies
}