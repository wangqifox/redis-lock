local key = KEYS[1]
local timeout = ARGV[1]
local result = redis.call('setnx', key, 1)
if (result == 1) then
    redis.call('expire', key, timeout)
end
return result