local key = KEYS[1]
local timestamp = ARGV[1]
local result = redis.call("get", key)

if (result == timestamp) then
    return redis.call("del", key)
else
    return 0
end
