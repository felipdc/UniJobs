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

const beforeAll = async (req) => {
  console.log('[METHOD]')
  console.log(req.method)
  console.log('[HEADER]')
  console.log(req.headers)
  if (req.method !== 'GET')  {
    console.log('[BODY]')
    const body = await json(req)
    console.log(body)
  }
}
  
const createService = async (req, res) => {
  beforeAll(req)

	const body = await json(req)

	const jwt = await getJwtAuth(req, res)

	if (!body.name || body.isOffer === undefined) throw createError(400, 'Bad params. Service name and isOffer is required')

	if (!isUser(jwt) && !isAdmin(jwt)) throw createError(403, 'Forbidden. Only users and admins can create services')

	const servicesProperties = Object.assign({},
    {name: body.name},
    {isOffer: body.isOffer},
    {image: body.image},
    {createdBy: jwt.id},
    {description: body.description}
  )
	
	const newService = new Service(servicesProperties)

	const service = await newService.save()
	  .then(() => console.log('Service saved'))
	  .catch((err) => { throw createError(500, 'Could not create service in db') })

	return newService
}

const getService = async (req, res) => {
  beforeAll(req)

	const queryString = await req.query

	const id = queryString.id

  const createdBy = queryString.createdBy

	const isOffer = queryString.isOffer

  // Return all types of services
	if (!id) {
    if (!createdBy) {
      if (isOffer) {
        const servicesArr = await Service.find({ isOffer }, (err, services) => {
          if (err) throw createError(500, 'Could not retrieve services from db')
          return services
        })
        return servicesArr
      } else {
        const servicesArr = await Service.find({ }, (err, services) => {
          if (err) throw createError(500, 'Could not retrieve services from db')
          return services
        })
        return servicesArr
      }
    } else {
        const servicesArr = await Service.find({ createdBy }, (err, services) => {
          if (err) throw createError(500, 'Could not retrieve services from db')
          return services
        })
        return servicesArr      
    }
  }
  // Return specific service if id is sent
  if (id) {
    const service = await Service.findById(id, (err, service) => {
      if (err) throw createError(500, 'Could not retrieve service from db')
      return service
    })
    if (!service) throw createError(404, 'Service not found')
    return service
  }
}

const updateService = async (req, res) => {
  beforeAll(req)

	const jwt = await getJwtAuth(req, res)

	const { id, name, description, image, isOffer, active } = await json(req)

	if (!id) throw createError(400, 'Bad params. Service id is required')

	const serviceToUpdate = await Service.findById({ _id: id }, (err, service) => {
		if (err) throw createError(500, 'Could not retrieve service from db')
		return service
	})

	if (!serviceToUpdate) throw createError(404, 'Service not found')

	// Check if user thats making the request created the service
	// Note that admin can update services no matter if he created or not
	if (jwt.id !== serviceToUpdate.createdBy && !isAdmin(jwt)) throw createError(403, 'Forbidden. User does not have permission to update this service')

	 const toUpdate = Object.assign({},
	 	name && { name },
    description && { description },
    isOffer && { isOffer },
    image  && { image },
    active === undefined ? null : { active }
  )

	const updatedReq = await Service.findOneAndUpdate(
		{ _id: id },
		toUpdate,
		(err, service) => {
    if (err) throw createError(500, 'Could not update service on db')
    console.log(`Sucessfully updated service with id = ${id}`)
    return service
	})

	return serviceToUpdate	
}

const deleteService = async (req, res) => {
  beforeAll(req)

	const jwt = await getJwtAuth(req, res)

	if (!isAdmin(jwt) && !isSystem(jwt)) throw createError(403, 'Forbidden. Only system and admin can delete services')

	const { id } = await json(req)

	if (!id) throw createError(400, 'Bad params. Service id is required')

	const serviceToDelete = await Service.findById({ _id: id }, (err, service) => {
		if (err) throw createError(500, 'Could not retrieve service from db')
		return service
	})

	if (!serviceToDelete) throw createError(404, 'Not found. Service does not exist')

	const deleteReq = await Service.findOneAndDelete({ _id: id }, (err, service) => {
    if (err) throw createError(500, 'Could not remove service from db')
    console.log(`Sucessfully deleted service with id = ${id}`)
    return service
	})

	return serviceToDelete
}

const likeService = async (req, res) => {
	const jwt = await getJwtAuth(req, res)
	const body = await json(req)

	if (!isUser(jwt)) throw createError(403, 'You have to be an user to proceed with this action')

	var throwDoubleLike = () => {
		throw createError(400, "Bad request. User has already liked this service")
	}

  const serviceId = body.id

  const service = await Service.findOne({ _id: serviceId })

  console.log(service)

  const likedBy = service.likedBy

  if (likedBy.length !== 0) {
    const alreadyLiked = likedBy.find((userId) => {
      return userId === jwt.id
    })

    if (alreadyLiked) throwDoubleLike()
  }

  likedBy.push(jwt.id)

	var setLikeOnDb = async () => {
		const toUpdate = {likedBy}

		const updatedReq = await Service.findOneAndUpdate(
			{ _id: serviceId },
			toUpdate,
			(err, service) => {
	    if (err) throw createError(500, 'Could not update service on db')
	    console.log(`Sucessfully updated service with id = ${serviceId}`)
	    return service
		})
 	}

	await setLikeOnDb()

  return likedBy
}

module.exports = {
	createService,
	getService,
	deleteService,
	updateService,
	likeService
}