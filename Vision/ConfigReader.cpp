#include "ConfigReader.h"

#include <exception>
#include <fstream>
#include <iostream>
#include <utility>

ConfigReader::ConfigReader(const std::string& filename)
{
	std::string key;
	std::string value;

	std::ifstream f(filename);

	while (f >> key && f >> value)
	{
		configs.emplace(std::make_pair(key, value));
	}
}

bool ConfigReader::GetBool(const std::string& key, bool b) const
{
	std::string debugStr = "UNDEFINED";
	try
	{
		auto itr = configs.find(key);
		if (itr != configs.end())
		{
			debugStr = itr->second;
			if (itr->second == "true")
			{
				return true;
			}
			else if (itr->second == "false")
			{
				return false;
			}
			else
			{
				throw (std::runtime_error("invalid conversion"));
			}
		}
		else
		{
			return b;
		}
	}
	catch (const std::exception& e)
	{
		std::cerr << "Exception found when reading " << key << ":" << debugStr << " into bool: " << e.what() << std::endl;
		return b;
	}
}

double ConfigReader::GetDouble(const std::string& key, double d) const
{
	std::string debugStr = "UNDEFINED";
	try
	{
		auto itr = configs.find(key);
		if (itr != configs.end())
		{
			debugStr = itr->second;
			return std::stod(itr->second);
		}
		else
		{
			return d;
		}
	}
	catch (const std::exception& e)
	{
		std::cerr << "Exception found when reading " << key << ":" << debugStr << " into double: " << e.what() << std::endl;
		return d;
	}
}

unsigned int ConfigReader::GetUInt(const std::string& key, unsigned int u) const
{
	std::string debugStr = "UNDEFINED";
	try
	{
		auto itr = configs.find(key);
		if (itr != configs.end())
		{
			debugStr = itr->second;
			return std::stoul(itr->second);
		}
		else
		{
			return u;
		}
	}
	catch (const std::exception& e)
	{
		std::cerr << "Exception found when reading " << key << ":" << debugStr << " into uint: " << e.what() << std::endl;
		return u;
	}
}

int ConfigReader::GetInt(const std::string& key, int i) const
{
	std::string debugStr = "UNDEFINED";
	try
	{
		auto itr = configs.find(key);
		if (itr != configs.end())
		{
			debugStr = itr->second;
			return std::stol(itr->second);
		}
		else
		{
			return i;
		}
	}
	catch (const std::exception& e)
	{
		std::cerr << "Exception found when reading " << key << ":" << debugStr << " into int: " << e.what() << std::endl;
		return i;
	}
}

std::string ConfigReader::GetString(const std::string& key, std::string s) const
{
	std::string debugStr = "UNDEFINED";
	try
	{
		auto itr = configs.find(key);
		if (itr != configs.end())
		{
			debugStr = itr->second;
			return itr->second;
		}
		else
		{
			return s;
		}
	}
	catch (const std::exception& e)
	{
		std::cerr << "Exception found when reading " << key << ":" << debugStr << " into string: " << e.what() << std::endl;
		return s;
	}
}

