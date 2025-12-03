1. Added a universal event API that supports event registration, triggering, and cancellation.
2. Added a universal registration API that supports both vanilla registries and Brick Lib's additional registries (Note: Lower versions will include higher version registries after compatibility updates, but these are only placeholders, and the registered object type is Object!).
3. Added a universal network API that supports login packets from the server to the client and in-game regular network packets. It also allows sending strings directly between both ends and monitoring sending/receiving via events.
4. Added a universal configuration file API, using Forge-style configuration files for configuration management.
5. Supports universal data generators, update checkers, and client-side commands.
6. Provides support for custom JSON functions, allowing registered handlers to be called within JSON and return values.
7. Added the ResourceID class for partial replacement of the ResourceLocation class
