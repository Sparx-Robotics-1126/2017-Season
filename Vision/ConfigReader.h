#ifndef CONFIG_READER_H
#define CONFIG_READER_H

#include <map>
#include <string>

class ConfigReader
{
public:
	ConfigReader(const std::string& filename);

	bool GetBool(const std::string& key, bool b) const;
	double GetDouble(const std::string& key, double d) const;
	unsigned int GetUInt(const std::string& key, unsigned int u) const;
	int GetInt(const std::string& key, int i) const;
	std::string GetString(const std::string& key, std::string s) const;
private:
	std::map<std::string, std::string> configs;
};

#endif

