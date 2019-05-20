// Micro deps
const { json, createError } = require('micro')

// Utils
const connectWithDB = require('./db')
const { isUser, isAdmin, isSystem } = require('./authCheck')
const { getJwtAuth } = require('./jwtHelper')
const { auth: authConfig } = require('./config')[process.env.NODE_ENV || 'development']

// Service model
const Service = require('./models/Service')

const db = connectWithDB()

const createService = async (req, res) => {
	const body = await json(req)

	const jwt = await getJwtAuth(req, res)

	if (!body.name || !body.location || !body.isOffer) throw createError(400, 'Bad params. Service name, location and isOffer is required')

	if (!isUser(jwt) && !isAdmin(jwt)) throw createError(403, 'Forbidden. Only users and admins can create services')

	const servicesProperties = Object.assign({},
    {name: body.name},
    {location: body.location},
    {isOffer: body.isOffer},
    {createdBy: jwt.id},
    body.description && { description }
  )
	
	const newService = new Service(servicesProperties)

	const service = await newService.save()
	  .then(() => console.log('Service saved'))
	  .catch((err) => { throw createError(500, 'Could not create service in db') })

	return newService
}

const getService = async (req, res) => {

	const queryString = await req.query

	const id = queryString.id

	const isOffer = queryString.isOffer

	const jwt = await getJwtAuth(req, res)

	if (!isAdmin(jwt) && !isUser(jwt)) throw createError(403, 'Forbidden. Only users can see services')

	// If id was not send in request, return all services
	
	if (!id) {
		// Retrieve all users from db if it is admin
		const servicesArr = await Service.find({isOffer}, (err, services) => {
			if (err) throw createError(500, 'Could not retrieve services from db')
			return services
		})

		return servicesArr
	} else {
	  const service = await Service.findById(id, (err, service) => {
	    if (err) throw createError(500, 'Could not retrieve service from db')
	    return service
	  })

	  return service
	}
}

const deleteService = (req, res) => {
	const jwt = await getJwtAuth(req, res)

	if (!jwt.isAdmin && !jwt.isSystem) throw createError(403, 'Forbidden. Only system and admin can delete services')

	const { id } = await json(req)

	if (!id) throw createError(400, 'Bad params. Service id is required')

	const serviceToDelete = await Service.findById({ _id: id }, (err, service) => {
		if (err) throw createError(500, 'Could not retrieve service from db')
		return service
	})

	if (!service) throw createError(404, 'Not found. Service does not exist')

	  // Fix this hacky thing later
  let deletedUser = null

	const deleteReq = await Service.findOneAndDelete({ _id: id }, (err, service) => {
    if (err) throw createError(500, 'Could not remove service from db')
    console.log(`Sucessfully deleted service with id = ${id}`)
    return service
	})

	return serviceToDelete
}

module.exports = {
	createService,
	getService,
	deleteService
}