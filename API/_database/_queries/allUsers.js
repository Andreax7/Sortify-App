const db = require('../connection.js')

async function register(body, password, picture) {
  return new Promise((resolve, reject) => {
    var sql ='INSERT INTO users (firstName, lastName, email, role, active, picture, password) VALUES (?,?,?,?,?,?,?)'
    var data = [body.firstName, body.lastName, body.email, 0, 1, picture, password];
    db.run(sql, data, (err, rows) => {
      if (err) { reject(err) }
      resolve({"email": body.email, "password":password})
    })
  })
}

function getUserData(email){
  return new Promise((resolve, reject) => {
    let result = []
  //  console.log('db ', email)
    db.get(`SELECT * FROM users WHERE email = ? ;`, [email], (err, rows) => {
      if(err){ 
        reject(err)
        console.log('err ', err) }
      else {
        result.push(rows)
      resolve(result[0])
      }
    })
  })
}

function updateUserData(body,uid){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE users 
            SET firstName = ?,
              lastName = ?, 
              email = ?,
              picture = ?
            WHERE userId = ?;`,
    [body.firstName, body.lastName, body.email, body.picture, parseInt(uid)],
    (err, result) => {
        if (err) reject(err)
        resolve('User updated')
      }
    );
  })
}

function updatePassword(uid,password){
  return new Promise((resolve, reject) => {
    db.run(`UPDATE users 
            SET password = ?
            WHERE userId = ?;`,
    [password, parseInt(uid)],
    (err, result) => {
        if (err) reject(err)
        resolve("password updated")
      }
    );
  })

}

function getAllTrashTypes() {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM types;`, (err, rows) => {
      if(err) { reject(err) }
      result.push(rows)
      resolve(result)
    })
  })
}


const getAllProducts = () => {
  return new Promise((resolve, reject) => {
      db.all('SELECT * FROM products', [], (err, rows) => {
          if (err) {
              console.error('Error retrieving products from database:', err.message);
              reject(err);
          } else {
              resolve(rows);
          }
      });
  });
};
/*
function getAllProducts() {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM products;`, (err, rows) => {
      if(err) { reject(err) }
      
      if(rows !== undefined ){
          result.push(rows)
      } 
      resolve(result)
    }, 
  )
  })
}*/
function scanProduct(barcode) {
  return new Promise((resolve, reject) => {
    // Use db.get to fetch a single row based on the barcode
    db.get(`SELECT * FROM products WHERE barcode = ?;`, [barcode], (err, row) => {
      if (err) {
        reject(err); // Reject the promise if there's an error
        return; // Exit the function after rejecting to prevent further execution
      }
      
      if (row) {
        resolve(row); // Resolve with the row if a matching row is found
      } else {
        resolve(undefined); // Resolve with undefined if no matching row is found
      }
    });
  });
}



async function getProductById(pid) {
  return new Promise((resolve, reject) => {
    db.all(`SELECT * FROM products WHERE productId = ? ;`, [pid], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows); // `rows` will be an array of all matching rows
      }
    });
  });
}
 
async function productsByType(tid) {
  return new Promise((resolve, reject) => {
    let result = [];
    db.all(`SELECT * FROM products WHERE typeId = ?;`, [tid], (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows);  // `rows` will already be an array of all matching rows
      }
    });
  });
}



async function sendRequest(body, uid, date) {
  return new Promise((resolve, reject) => {
    var sql = 'INSERT INTO forms (userId, productName, date, seen, productdetails, barcode, typeId, productImage) VALUES (?,?,?,?,?,?,?,?)';
    var data = [
      uid,
      body.productName,
      date,
      0,
      body.productdetails,
      body.barcode,
      body.typeId ,
      body.productImage
    ];

    db.run(sql, data, (err, rows) => {
      if (err) { reject(err) }
      resolve(rows)
    })
  })
}


function myRequests(uid) {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM forms WHERE userId = ? ;`, [uid], (err, rows) => {
      if(err){ reject(err) }
        result.push(rows)
      }, () => {
        resolve(result)
      })
  })

}


function requestInfo(fid) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`SELECT * FROM forms WHERE formId = ? ;`, [fid], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result[0])
      }
    })
  })

}

function getContainerLocationsByType(typeId) {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM containers WHERE active = ? AND typeId = ? ;`, [1,typeId], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result)
      }
    })
  })
}

function getContainerLocations() {
  return new Promise((resolve, reject) => {
    db.all(`SELECT * FROM containers WHERE active = ?;`, [1], (err, rows) => {
      if(err){ 
        console.log('Error during query:', err);
        reject(err);
      }
      else {
       // console.log('Fetched rows:', rows);
        resolve(rows);
      }
    });
  })
}

async function myRecycledStat(uid) {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM recycled WHERE userId = ? ;`, [uid], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result)
      }
    })
  })
}

async function AllRecycled() {
  return new Promise((resolve, reject) => {
    db.all(`SELECT * FROM recycled;`, (err, rows) => {
      if(err){ reject(err) }
      else {
      resolve(rows)
      }
    })
  })
}


async function throwTrash(uid, body, date) {

  return new Promise((resolve, reject) => {
      let result = [];
      db.run(`INSERT INTO recycled (userId, containerId, quantity, date) VALUES (?,?,?,?);`,
          [parseInt(uid), parseInt(body.containerId), parseFloat(body.quantity) , date], (err) => {
              if (err) {
                  console.log(err);
                  return reject(err);
              }
              resolve("successfully recycled");
          }
      );
  });
}



module.exports = {
  register,
  getUserData, 
  updateUserData,
  updatePassword,
  getAllTrashTypes,
  getAllProducts,
  scanProduct,
  getProductById,
  productsByType,
  sendRequest,
  myRequests,
  requestInfo,
  getContainerLocationsByType,
  getContainerLocations,
  myRecycledStat,
  throwTrash,
  AllRecycled
 
}