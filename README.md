
# hibernate-base-repository
Implementation of Repository pattern for hibernate.


About
-
This repository contains implementation of the repository pattern for hibernate which can be found in
com.cyecize.baserepository.BaseRepository

Features
-
It provides some useful methods that otherwise you would need to implement in each repository and 
that is not really good since the logic is pretty much the same.
BaseRepository also provides type safety as each repository is for a specific entity.

Here are the methods that are already implemented:
* void  persist(E  entity)
* void  merge(E  entity)
* void  remove(E  entity)
* Long  count()
* E  find(ID  id)
* List<E>  findAll()

Where 'E' is the type of the entity and 'ID' is the type of the primary key for that entity.

Usage
-
You can see the example repositories that are in the demo folder and see from there.

Execute around pattern is used to reduce boilerplate code and focus on the main goal of your DB call.

To use the base repository in your project, you can copy the BaseRepository.java and paste it in your code.
You might need to make minor changes based on your platform, but it should work fine for most applications.

For Spring, you can create a bean for EntityManager and it will be automatically resolved.

Contacts
-

If you have questions, you can contact me at ceci2205@abv.bg
