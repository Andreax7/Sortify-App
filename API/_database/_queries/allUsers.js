const db = require('../connection.js')

async function register(body, password, picture) {
  return new Promise((resolve, reject) => {
    var sql ='INSERT INTO users (firstName, lastName, email, role, active, picture, password) VALUES (?,?,?,?,?,?,?)'
    var data = [body.firstName, body.lastName, body.email, 0, 1, picture, password];
    db.run(sql, data, (err, rows) => {
      if (err) { reject(err) }
      resolve({"name":body.firstName, "email": body.email, "password":password})
    })
  })
}

function getUserData(email){
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM users WHERE email = ? ;`, [email], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result[0])
      }
    })
  })
}
function getAllTrashTypes() {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM types;`, (err, rows) => {
      if(err) { reject(err) }
      result.push(rows)
    }, () => {
      resolve(result)
    })
  })
}

function getAllProducts() {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM products;`, (err, rows) => {
      if(err) { reject(err) }
      result.push(rows)
    }, () => {
      resolve(result)
    })
  })
}

function scanProduct(barcode) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`SELECT * FROM products WHERE barcode = ? ;`, [barcode], (err, rows) => {
      if(err) { reject(err) }
      result.push(rows)
    }, () => {
      resolve(result)
    })
  })

}

function getProduct(pid) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`SELECT * FROM products WHERE productId = ? ;`, [pid], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result[0])
      }
    })
  })
}


function productsByType(tid) {
  return new Promise((resolve, reject) => {
    let result = []
    db.each(`SELECT * FROM products WHERE typeId = ? ;`, [tid], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result)
      }
    })
  })
 
}

async function sendRequest(body, uid) {
  return new Promise((resolve, reject) => {
    var sql ='INSERT INTO forms (userId, productInfo, date, seen) VALUES (?,?,?,?)'
    var data = [uid, body.productInfo, body.date, 0];
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
    let result = []
    db.each(`SELECT * FROM containers WHERE active = ?;`, [1], (err, rows) => {
      if(err){ reject(err) }
      else {
        result.push(rows)
      resolve(result)
      }
    })
  })
}

function myRecycledStat(uid) {
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

function throwTrash(uid, body) {
  return new Promise((resolve, reject) => {
    let result = []
    db.run(`INSERT INTO recycled (userId, containerId, date) VALUES (?, ?, ?);`,
          [parseInt(uid), parseInt(body.containerId), body.date], 
          (err, rows) => {
            console.log(rows)
                if(err) { reject(err) }
                result.push(rows)
            },
          () => { resolve("successfully recycled ") }
    )
  })
}

module.exports = {
  register,
  getUserData, 
  getAllTrashTypes,
  getAllProducts,
  scanProduct,
  getProduct,
  productsByType,
  sendRequest,
  myRequests,
  requestInfo,
  getContainerLocationsByType,
  getContainerLocations,
  myRecycledStat,
  throwTrash
 
}