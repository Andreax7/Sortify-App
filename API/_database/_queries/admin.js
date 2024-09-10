const db = require('../connection.js')


function getProductByName(pName) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`SELECT * FROM products 
            WHERE productName = ?`,
      [parseInt(pName)],
      (err, rows) => {
          if (err){
             reject(err)
          }
            resolve("Successfully added")
      },
      () => { resolve(result) }
    );
  }) 
}
function addNewProduct(body){
  return new Promise((resolve, reject) => {
    db.run(`INSERT INTO products (barcode, productName, image, details, typeId) VALUES (?, ?, ?, ?, ?);`,
          [body.barcode, body.productName, body.image, body.details, body.typeId], 
          (err, rows) => {
            if (err){
                reject(err)
            }
            resolve("Successfully added")
        }
    )
  })
}

function removeProduct(productId){
  return new Promise((resolve, reject)=> {
    db.run(`DELETE FROM products WHERE productId = ?`,
        productId,
        (err, result) => {
          console.log('delete product '+ productId)
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
              SET productName = ?, typeId = ?, barcode = ?, image = ?, details = ?
              WHERE productId = ?`,
      [body.productName, parseInt(body.typeId), body.barcode, body.image, body.details, parseInt(id)],
      (err, result) => {
            if (err) {
                reject(err)
            }
            resolve("Successfully updated")
        }
    );
  })
}

function addNewType(body){
  return new Promise((resolve, reject) => {
    db.run(`INSERT INTO types (typeName, info) VALUES (?, ?);`,
          [body.typeName, body.info], 
          (err, result) => {
              if (err){
                  reject(err)
              }
              resolve("Successfully added")
          }
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

function removeProductsOfDeletedType(typeId){
  return new Promise((resolve, reject)=> {
    db.run(`DELETE FROM products WHERE typeId = ?`,
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
              if (err) {
                reject(err)
            }
            resolve("Successfully updated")
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

async function setUserAdmin(role, uid){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE users 
            SET role = ?
            WHERE userId = ?`,
      [ parseInt(role), parseInt(uid)],
      function (err) {
        if (err) return reject(err);
        if (this.changes === 0) {
            return reject(new Error("No user found to update"));
        }
        resolve(uid + " user successfully updated");
    }
    );
  })
}

async function changeProfileActivity(active, uid) { 
  return new Promise((resolve, reject) => {
      db.run(`UPDATE users 
              SET active = ?
              WHERE userId = ?`,
      [active, parseInt(uid)],
      function (err) {
        if (err) return reject(err);
        if (this.changes === 0) {
            return reject(new Error("No user found to update"));
        }
        resolve({ message: `${uid} user successfully updated` });
      }
    )
  });
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
          resolve(result)
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
        resolve(fid + " user request successfully updated")
      }
    );
  })
}

function addContainer(body){
  result = []
  return new Promise((resolve, reject) => {
    db.run(`INSERT INTO containers (longitude, latitude, active, typeId) VALUES (?, ?, ?, ?);`,
      [body.longitude ,body.latitude, 1, body.typeId],  
          (err, rows) => {

            console.log(rows)
            if(err) { reject(err) }
            
            result.push(rows)
          
            resolve("Successfully added")
        }
    )
  })
  
}
function AllContainerLocations() {
  return new Promise((resolve, reject) => {
    let result = [];
    db.all(`SELECT * FROM containers;`, (err, rows) => {
      if (err) {
        reject(err); // Reject the promise with the error
      } else {
        result = rows; // Directly assign the rows to result
        resolve(result); // Resolve the promise with the result
      }
    });
  });
}


function updateContainer(body,cid) {
  return new Promise((resolve, reject) => {
    db.run(
      `UPDATE containers 
       SET longitude = ?, latitude = ?, active = ?, typeId = ? 
       WHERE containerId = ?;`,
      [body.longitude, body.latitude, body.active, body.typeId, parseInt(cid)],
      function (err) {
        if (err) {
          reject(err);
        } else {
          resolve({ message: 'Update successful', changes: cid });
        }
      }
    );
  });
}

/*
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
}*/

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

function filterContainer(tid){
  return new Promise((resolve, reject) => {
    let result = []
    db.all(`SELECT * FROM containers 
            WHERE typeId=? ;`, [tid],
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
  removeProductsOfDeletedType,
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
  filterContainer,
  updateContainer,
  addOccupancy,
  getContainerOccupancy,
  updateContainerOccupancy,
  getAllOccupancies
}